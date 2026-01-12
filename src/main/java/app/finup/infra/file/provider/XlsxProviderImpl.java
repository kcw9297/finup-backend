package app.finup.infra.file.provider;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * XlsxFileProvider 구현 클래스
 * @author kcw
 * @since 2025-12-25
 */
@Slf4j
@Component
public class XlsxProviderImpl implements XlsxProvider {


    @Override
    public List<Map<String, String>> extractRows(byte[] fileBytes, int sheetIndex) {
        return doExtractRows(fileBytes, sheetIndex, List.of());
    }


    @Override
    public List<Map<String, String>> extractRows(byte[] fileBytes, int sheetIndex, List<Integer> extractColumnIdxes) {
        return doExtractRows(fileBytes, sheetIndex, extractColumnIdxes);
    }


    // Row 추출 로직
    private List<Map<String, String>> doExtractRows(byte[] fileBytes, int sheetIndex, List<Integer> extractColumnIdxes) {

        return doExtract(fileBytes, sheetIndex, sheet -> {

            // [1] 결과를 담을 컬렉션 및 헤더 정보 초기화
            List<Map<String, String>> result = new ArrayList<>();
            Map<Integer, String> headers = getHeaderIdxValueMap(sheet.getRow(0)); // Map<Idx, 헤더이름>

            // [2] 추출 수행
            for (Row row : sheet) {

                // 0번째 Row인 경우, 이미 헤더 정보는 추출했으므로 건너뜀
                if (row.getRowNum() == 0) continue;

                // 결과를 저장할 Map 선언 (row 정보를 담을 map)
                Map<String, String> rowMap = new HashMap<>();

                // 추출할 row 인덱스 목록 유무에 따라 다르게 처리
                if (extractColumnIdxes.isEmpty()) // 모든 row를 추출하는 경우 (딱히 명시하지 않은 경우)
                    IntStream.range(0, row.getLastCellNum()).forEach(idx -> doExtractRow(row, idx, headers, rowMap));

                // 특정 row만 추출하는 경우
                else extractColumnIdxes.forEach(idx -> {
                    if (!headers.containsKey(idx)) throw new ProviderException(AppStatus.XLSX_INVALID_COLUMN_INDEX);
                    doExtractRow(row, idx, headers, rowMap);
                });

                // 현재 row 추출 결과 삽입
                result.add(rowMap);
            }

            // [3] 추출이 완료된 리스트 반환
            return result;
        });
    }


    @Override
    public Map<String, List<String>> extractColumns(byte[] fileBytes, int sheetIndex) {
        return doExtractColumns(fileBytes, sheetIndex, List.of());
    }


    @Override
    public Map<String, List<String>> extractColumns(byte[] fileBytes, int sheetIndex, List<String> extractHeaders) {
        return doExtractColumns(fileBytes, sheetIndex, extractHeaders);
    }


    // 컬럼 값 추출 수행
    private Map<String, List<String>> doExtractColumns(byte[] fileBytes, int sheetIndex, List<String> extractHeaders) {

        return doExtract(fileBytes, sheetIndex, sheet -> {

            // [1] 시트 내 Row를 참조하기 전, 결과 Map 초기화
            Map<String, Integer> headers = getHeaderValueIdxMap(sheet.getRow(0)); // Map<Idx, 헤더이름>
            Map<String, List<String>> result = new ConcurrentHashMap<>();

            // [2] 컬력션 초기화 수행
            if (extractHeaders.isEmpty()) // 모두 추출하는 경우
                headers.forEach((header, idx) -> result.put(header, new ArrayList<>()));

            // 특정 헤더만 추출하는 경우
            else extractHeaders.forEach(header -> {
                if (!headers.containsKey(header)) throw new ProviderException(AppStatus.XLSX_HEADER_NOT_EXIST);
                result.put(header, new ArrayList<>());
            });

            // [3] 추출 수행
            for (Row row : sheet) {

                // 0번째 Row인 경우, 이미 헤더 정보는 추출했으므로 건너뜀
                if (row.getRowNum() == 0) continue;

                // 추출할 column 목록 유무에 따라 다르게 처리
                result.forEach((header, columnValues) -> doExtractColumn(row, headers, header, columnValues));
            }

            // [4] 추출이 완료된 Map 반환
            return result;
        });

    }


    @Override
    public byte[] createXlsx(List<String> headers, List<Map<String, String>> rows) {
        return doCreateXlsx(headers, rows, "Sheet1");
    }


    @Override
    public byte[] createXlsx(List<String> headers, List<Map<String, String>> rows, String sheetName) {
        return doCreateXlsx(headers, rows, sheetName);
    }


    private byte[] doCreateXlsx(
            List<String> headers, List<Map<String, String>> rows, String sheetName) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // [1] 시트 생성 (기본 시트명)
            Sheet sheet = workbook.createSheet(sheetName);

            // [2] 헤더 행 & 데이터 행 생성
            createHeaderRow(headers, sheet);
            createDataRows(headers, rows, sheet);

            // [3] 바이트 배열로 변환
            workbook.write(out);
            return out.toByteArray();


        } catch (ProviderException e) {
            throw e;

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "예상 외 이유로 xlsx 파일 생성 시도 실패! 원인 : %s", e.getMessage());
            throw new ProviderException(AppStatus.XLSX_CREATE_FAILED);
        }
    }

    // 헤더 row 생성
    private void createHeaderRow(List<String> headers, Sheet sheet) {

        // 중복 헤더 검증
        checkHeaderDuplication(headers);

        // 헤더 row 생성 수행
        Row headerRow = sheet.createRow(0);
        IntStream.range(0, headers.size())
                .forEach(idx -> {

                    // 헤더 셀 생성
                    Cell headerCell = headerRow.createCell(idx);

                    // 헤더 값 추출 및 검증
                    String header = headers.get(idx);
                    if (Objects.isNull(header) || header.isBlank()) throw new ProviderException(AppStatus.XLSX_INVALID_HEADER);

                    // 헤더 셀 내 값 삽입
                    headerCell.setCellValue(header);
                });
    }


    // 헤더 중복 검증
    private void checkHeaderDuplication(List<String> headers) {

        Set<String> headerSet = new HashSet<>();
        headers.forEach(header -> {

            // 유효하지 않은 헤더가 입력된 경우
            if (Objects.isNull(header) || header.isBlank())
                throw new ProviderException(AppStatus.XLSX_INVALID_HEADER);

            // 중복 헤더 값을 입력한 경우 (set 삽입 실패)
            if (!headerSet.add(header)) {
                LogUtils.showError(this.getClass(), "중복된 헤더를 포함한 생성 요성. 헤더 이름 :%s", header);
                throw new ProviderException(AppStatus.XLSX_DUPLICATION_HEADER);
            }
        });
    }


    // 데이터 row 일괄 생성
    private void createDataRows(List<String> headers, List<Map<String, String>> rows, Sheet sheet) {
        IntStream.range(0, headers.size())
                .forEach(idx -> {

                    // 현재 데이터 cell을 담을 row 생성
                    Row dataRow = sheet.createRow(idx + 1);

                    // row 데이터 추출 후 검증 (정상 존재하는지)
                    Map<String, String> row = rows.get(idx);
                    if (Objects.isNull(row) || row.isEmpty()) throw new ProviderException(AppStatus.XLSX_INVALID_ROW);

                    // 생성한 row 내 cell 삽입
                    IntStream.range(0, row.size())
                            .forEach(colIdx -> {
                                String header = headers.get(colIdx);
                                String value = row.get(header);
                                dataRow.createCell(colIdx).setCellValue(Objects.isNull(value) ? "" : value);
                            });
                });
    }


    // 파일 추출작업 수행
    private <T> T doExtract(byte[] fileBytes, int sheetIndex, Function<Sheet, T> extractMethod) {

        // WorkBook(Sheet 집합) 조회 후, 필요 시트 내 파일 추출
        try (Workbook workbook = getWorkbook(fileBytes)) {

            // [1] 지정한 위치의 시트 조회
            Sheet sheet = workbook.getSheetAt(sheetIndex);

            // [2] 추출 수행
            return extractMethod.apply(sheet);

        } catch (ProviderException e) {
            throw e;

        }  catch (Exception e) {
            LogUtils.showError(this.getClass(), "예상 외 이유로 xlsx 파일 추출 시도 실패! 원인 : %s", e.getMessage());
            throw new ProviderException(AppStatus.XLSX_EXTRACT_FAILED);
        }
    }



    // 엑셀 파일을 읽고, WorkBook 객체로 변환 (엑셀 파일 내부 시트정보들의 집합)
    private Workbook getWorkbook(byte[] fileBytes) throws IOException {

        // [1] 검증 - 파일 바이트 배열이 존재하지 않는 경우 예외 반환
        if (Objects.isNull(fileBytes) || fileBytes.length == 0)
            throw new ProviderException(AppStatus.XLSX_FILE_NOT_EXIST);

        // [2] 파일 바이트로 부터 WorkBook 객체 생성
        return new XSSFWorkbook(new ByteArrayInputStream(fileBytes));
    }


    // 헤더 추출 (Map 형태)
    private Map<Integer, String> getHeaderIdxValueMap(Row row) {

        // 헤더 row가 존재하지 않는 경우
        if (Objects.isNull(row))
            throw new ProviderException(AppStatus.XLSX_HEADER_NOT_EXIST);

        // [1] Header 정보를 담을 Map 선언
        Map<Integer, String> headers = new HashMap<>();

        // [2] header 정보 추출
        row.forEach(cell -> {

            // 인덱스, 헤더 값 추출
            Map.Entry<Integer, String> idxHeaderEntry = checkAndGetCellIdxValueEntry(cell);

            // 삽입 수행
            Integer idx = idxHeaderEntry.getKey();
            String header = idxHeaderEntry.getValue();

            // 헤더 존재여부 검증 수행
            if (headers.containsValue(header)) {
                LogUtils.showError(this.getClass(), "엑셀 파일 내 중복 헤더 존재. 대상 헤더 : %s, 대상 헤더 idx : %d", header, idx);
                throw new ProviderException(AppStatus.XLSX_DUPLICATION_HEADER);
            }

            // 헤더 Map 삽입
            headers.put(idx, header);
        });

        // [3] 추출 결과 반환
        return headers;
    }


    // 헤더 추출 (Set 형태)
    private Map<String, Integer> getHeaderValueIdxMap(Row row) {

        // [1] Header 정보를 담을 Map 선언
        Map<String, Integer> headers = new HashMap<>();

        // [2] header 정보 추출
        row.forEach(cell -> {

            // 인덱스, 헤더 값 추출
            Map.Entry<Integer, String> idxHeaderEntry = checkAndGetCellIdxValueEntry(cell);

            // 삽입 수행
            Integer idx = idxHeaderEntry.getKey();
            String header = idxHeaderEntry.getValue();

            // 헤더 존재여부 검증 수행
            if (headers.containsKey(header)) {
                LogUtils.showError(this.getClass(), "엑셀 파일 내 중복 헤더 존재. 대상 헤더 : %s, 대상 헤더 idx : %d", header, idx);
                throw new ProviderException(AppStatus.XLSX_DUPLICATION_HEADER);
            }

            // 헤더 Map 삽입
            headers.put(header, idx);
        });

        // [3] 추출 결과 반환
        return headers;
    }


    // Cell 검증 후, 정상 존재 시 Entry<idx, cell값> 반환
    private Map.Entry<Integer, String> checkAndGetCellIdxValueEntry(Cell cell) {

        // 인덱스, 헤더 값 추출
        int idx = cell.getColumnIndex();
        String val = getCellValue(cell);

        // 헤더 값이 존재하지 않는 경우 예외 반환
        if (Objects.isNull(val)) {
            LogUtils.showError(this.getClass(), "xlsx 헤더 컬럼 추출 실패! 추출 시도 컬럼 idx : %d", idx);
            throw new ProviderException(AppStatus.XLSX_COLUMN_NOT_EXIST);
        }

        // 정상 존재하는 경우 <idx, cell값> Entry 반환
        return Map.entry(idx, val);
    }


    // Cell 값을 String 타입으로 추출
    private String getCellValue(Cell cell) {

        // cell이 존재하는 경우, Cell의 타입에 따라 캐스팅 후 반환
        return Objects.isNull(cell) ?
                null : // 셀이 유효하지 않으면 null
                switch (cell.getCellType()) {
                    case STRING     -> cell.getStringCellValue();
                    case NUMERIC    -> getStringNumeric(cell);
                    case BLANK      -> null; // 빈 값은 명시적으로 null 처리
                    default         -> null; // 유효하지 않은 타입이면 null
                };
    }


    // number 값 추출
    private String getStringNumeric(Cell cell) {

        // 날짜 데이터인 경우
        if (DateUtil.isCellDateFormatted(cell)) return cell.getLocalDateTimeCellValue().toString();

        // 일반적인 숫자인 경우
        double number = cell.getNumericCellValue();

        // 숫자 중에서도, 정수인 경우는 소숫점을 제거하고 반환
        return number == (long) number ? String.valueOf((long) number) : String.valueOf(number);
    }


    // row 데이터 추출
    private void doExtractRow(
            Row row, int idx, Map<Integer, String> headers, Map<String, String> rowMap) {

        // 헤더, 값 추출
        String header = headers.get(idx);
        String value = getCellValue(row.getCell(idx));

        // 두 값이 모두 유효하면 저장
        if (Objects.isNull(header)) throw new ProviderException(AppStatus.XLSX_HEADER_COLUMN_NOT_EXIST); // 유효하지 않은 헤더
        rowMap.put(header, value);
    }


    // column 데이터 추출
    private void doExtractColumn(
            Row row, Map<String, Integer> headers, String curHeader, List<String> columnValues) {

        // 현재 추출하려는 컬럼 인덱스 추출
        Integer curIdx = headers.get(curHeader);

        // 현재 컬럼 인덱스 값 추출 후 삽입
        columnValues.add(getCellValue(row.getCell(curIdx)));
    }


}


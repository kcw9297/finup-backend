package app.finup.infra.file.provider;

import java.util.List;
import java.util.Map;


/**
 * 엑셀 파일 조작 기능을 제공하는 Provider 인터페이스
 * @author kcw
 * @since 2025-12-25
 */
public interface XlsxProvider {


    /**
     * xlsx 파일 내 특정 컬럼 값 추출 (일괄 추출)
     * @param fileBytes          파일 바이트 배열 (스토리지로 부터 다운로드 받아온 배열)
     * @param sheetIndex         추출할 엑셀 파일의 시트번호 (주의: 0부터 시작)
     * @return Map<컬럼명, 컬럼데이터목록>
     */
    List<Map<String, String>> extractRows(byte[] fileBytes, int sheetIndex);


    /**
     * xlsx 파일 내 특정 컬럼 값 추출
     * @param fileBytes          파일 바이트 배열 (스토리지로 부터 다운로드 받아온 배열)
     * @param sheetIndex         추출할 엑셀 파일의 시트번호 (주의: 0부터 시작)
     * @param extractColumnIdxes 추출 대상 행 번호
     * @return Map<컬럼명, 컬럼데이터목록>
     */
    List<Map<String, String>> extractRows(byte[] fileBytes, int sheetIndex, List<Integer> extractColumnIdxes);


    /**
     * xlsx 파일 내 특정 컬럼 값 추출 (일괄 추출)
     * @param fileBytes      파일 바이트 배열 (스토리지로 부터 다운로드 받아온 배열)
     * @param sheetIndex     추출할 엑셀 파일의 시트번호 (주의: 0부터 시작)
     * @return Map<컬럼명, 컬럼데이터목록>
     */
    Map<String, List<String>> extractColumns(byte[] fileBytes, int sheetIndex);


    /**
     * xlsx 파일 내 특정 컬럼 값 추출
     * @param fileBytes      파일 바이트 배열 (스토리지로 부터 다운로드 받아온 배열)
     * @param sheetIndex     추출할 엑셀 파일의 시트번호 (주의: 0부터 시작)
     * @param extractHeaders 추출할 해더 이름 리스트
     * @return Map<컬럼명, 컬럼데이터목록>
     */
    Map<String, List<String>> extractColumns(byte[] fileBytes, int sheetIndex, List<String> extractHeaders);


    /**
     * xlsx 엑셀 파일 생성
     * @param headers 헤더 이름 목록
     * @param rows 엑셀 row 데이터 리스트 (Map<헤더명, cell값> 목록)
     * @return 생성된 xlsx 파일 바이트 배열 (다운로드 가능)
     */
    byte[] createXlsx(List<String> headers, List<Map<String, String>> rows);


    /**
     * xlsx 엑셀 파일 생성
     * @param headers   헤더 이름 목록
     * @param rows      엑셀 row 데이터 리스트 (Map<헤더명, cell값> 목록)
     * @param sheetName 엑셀 시트명
     * @return 생성된 xlsx 파일 바이트 배열 (다운로드 가능)
     */
    byte[] createXlsx(List<String> headers, List<Map<String, String>> rows, String sheetName);
}

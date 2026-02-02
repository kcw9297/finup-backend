package app.finup.infra.file.provider;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.LogUtils;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * CsvFileProvider 구현 클래스
 * @author kcw
 * @since 2026-01-07
 */
@Slf4j
@Component
public class CsvProviderImpl implements CsvProvider {

    @Override
    public List<Map<String, String>> extractRow(byte[] fileBytes) {
        return doExtractRow(fileBytes, List.of());
    }


    @Override
    public List<Map<String, String>> extractRow(byte[] fileBytes, List<Integer> extractColumnIdxes) {
        return doExtractRow(fileBytes, extractColumnIdxes);
    }


    // 실제 추출 로직 수행
    private List<Map<String, String>> doExtractRow(byte[] fileBytes, List<Integer> extractColumnIdxes) {

        // 결과를 담기 위한 리스트 선언
        List<Map<String, String>> result = new ArrayList<>();

        // 파일 읽기 수행
        try (CSVReader reader =
                     new CSVReader(new InputStreamReader(new ByteArrayInputStream(fileBytes), StandardCharsets.UTF_8))) {

            // [1] 파일 읽기 수행
            String[] headers = reader.readNext(); // 헤더 정보 추출 (첫 행에 존재)

            // [2] 검증
            // 만약 CSV 파일이 아무것도 없으면 예외 반환
            if (Objects.isNull(headers)) {
                LogUtils.showWarn(this.getClass(), "CSV 파일의 헤더가 유효하지 않습니다!");
                throw new ProviderException(AppStatus.FILE_EXTRACT_FAILED);
            }

            // 만약 행의 인덱스 범위를 초과하는 요청이 있는 경우 예외 반환
            if (Objects.isNull(extractColumnIdxes) ||
                    extractColumnIdxes.size() > headers.length ||
                    extractColumnIdxes.stream().anyMatch(idx -> idx < 0 || idx >= headers.length)) {

                LogUtils.showWarn(this.getClass(), "유효하지 않은 Column(열) 인덱스입니다! idx = %s",  extractColumnIdxes);
                throw new ProviderException(AppStatus.FILE_EXTRACT_FAILED);
            }

            // [3] 모든 행 데이터 읽기
            String[] row;
            while (Objects.nonNull(row = reader.readNext())) {

                // 현재 행의 열 데이터를 담기 위한 Map 선언 (순서 보장)
                Map<String, String> rowMap = new LinkedHashMap<>();

                // 추출 열 번호 존재 유무에 따라 다르게 처리
                if (extractColumnIdxes.isEmpty()) // 모든 열을 추출하는 경우
                    for (int i = 0; i < headers.length && i < row.length; i++) rowMap.put(headers[i], row[i]);

                else // 특정 열만 추출하는 경우
                    for (int idx : extractColumnIdxes) rowMap.put(headers[idx], row[idx]);

                // 결과 삽입
                result.add(rowMap);
            }

            // [4] 추출 결과가 담긴 리스트 반환
            return result;

        } catch (ProviderException e) {
            throw e;

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "CSV 파일 정보 추출에 실패했습니다! 원인 : %s", e.getMessage());
            throw new ProviderException(AppStatus.FILE_EXTRACT_FAILED);
        }


    }
}

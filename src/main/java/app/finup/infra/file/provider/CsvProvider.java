package app.finup.infra.file.provider;

import java.util.List;
import java.util.Map;


/**
 * CSV 파일 조작 기능을 제공하는 Provider 인터페이스
 * @author kcw
 * @since 2026-01-07
 */
public interface CsvProvider {

    /**
     * CSV 파일 내 row(행) 데이터 추출 (전체 행 추출)
     * @param fileBytes 파일 바이트 배열 (스토리지로 부터 다운로드 받아온 배열)
     * @return csv 파일 행 데이터 목록 (Map<컬럼명, 컬럼데이터> 리스트)
     */
    List<Map<String, String>> extractRow(byte[] fileBytes);


    /**
     * CSV 파일 내 row(행) 데이터 추출 (특정 행 인덱스만 골라 추출)
     * @param fileBytes 파일 바이트 배열 (스토리지로 부터 다운로드 받아온 배열)
     * @param extractColumnIdxes 추출 대상 행 번호
     * @return csv 파일 행 데이터 목록 (Map<컬럼명, 컬럼데이터> 리스트)
     */
    List<Map<String, String>> extractRow(byte[] fileBytes, List<Integer> extractColumnIdxes);

}

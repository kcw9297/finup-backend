package app.finup.infra.words.dto;


import app.finup.infra.words.utils.XmlAndJsonDtoUtil;
import lombok.Data;

/**
 * JSON 또는 XML로 응답오는 데이터 따로 받는 DTO
 * @author khj
 * @since 2025-12-11
 */

@Data
public class KsdApiResponse {
    private XmlAndJsonDtoUtil response;
}

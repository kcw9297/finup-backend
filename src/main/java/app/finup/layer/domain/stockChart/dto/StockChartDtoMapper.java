package app.finup.layer.domain.stockChart.dto;

import java.util.List;

public class StockChartDtoMapper {
    public static List<StockChartDto.CandleAi> toAi(List<StockChartDto.Detail> details){
        return details.stream()
                .limit(30)
                .map(d -> new StockChartDto.CandleAi(
                        d.getStck_bsop_date(),
                        Integer.parseInt(d.getStck_oprc()),
                        Integer.parseInt(d.getStck_hgpr()),
                        Integer.parseInt(d.getStck_lwpr()),
                        Integer.parseInt(d.getStck_clpr()),
                        Long.parseLong(d.getAcml_vol())
                ))
                .toList();
    }
}

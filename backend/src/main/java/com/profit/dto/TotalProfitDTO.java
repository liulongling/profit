package com.profit.dto;

import lombok.Data;


@Data
public class TotalProfitDTO extends TodayTaxationDTO {
    /**
     * 总盈利次数
     */
    private long totalProfitNumber;
    /**
     * 亏损次数
     */
    private long totalLossNumber;
    /**
     * 胜率
     */
    private String avgWinning;

    /**
     * 总收益
     */
    private Double totalProfit;

    /**
     * 持股市值
     */
    private Double stockValue;
    /**
     * 现金
     */
    private Double ready;
}

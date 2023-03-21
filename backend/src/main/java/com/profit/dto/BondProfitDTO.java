package com.profit.dto;

import lombok.Data;


@Data
public class BondProfitDTO {
    /**
     * 今日总收益
     */
    private Double todayProfit;
    /**
     * 交易金额
     */
    private Double transactionAmount;
    /**
     * 买入金额
     */
    private Double buyAmount;
    /**
     * 卖出金额
     */
    private Double sellAmount;
    /**
     * 佣金费用
     */
    private Double cost;
    /**
     * 交易次数
     */
    private int transactionNumber;
    /**
     * 最大一笔亏损
     */
    private Double maxLoss;
    /**
     * 最大一笔盈利
     */
    private Double maxProfit;
    /**
     * 盈利次数
     */
    private int profitNumber;
    /**
     * 亏损次数
     */
    private int lossNumber;
    /**
     * 胜率
     */
    private String winning;
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

    public void incrProfitNumber() {
        profitNumber++;
    }

    public void incrPlossNumber() {
        lossNumber++;
    }

    public int getTotalNumber() {
        return profitNumber + lossNumber;
    }

}

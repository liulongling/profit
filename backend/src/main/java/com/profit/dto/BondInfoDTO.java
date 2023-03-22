package com.profit.dto;

import com.profit.base.domain.BondInfo;
import lombok.Data;

@Data
public class BondInfoDTO extends BondInfo {
    /**
     * 短线收益
     */
    public Double stubProfit;
    /**
     * 网格收益
     */
    public Double gridProfit;
    /**
     * 持股数量
     */
    public long gpCount;
    /**
     * 短线持股数量
     */
    public long stubCount;
    /**
     * 网格持股数量
     */
    public long gridCount;
    /**
     * 持股数量盈亏
     */
    public Double gpProfit;
    /**
     * 当前总盈亏
     */
    public Double curProfit;
    /**
     * 总盈亏
     */
    public Double profit;
    /**
     * 上月总收益
     */
    public Double lastMonthProfit;
    /**
     * 本月总收益
     */
    public Double curMonthProfit;
    /**
     * 今日T收益
     */
    public Double todayTProfit;

    public Double getCurProfit() {
        return Double.parseDouble(String.format("%.2f", stubProfit + gridProfit + gpProfit));
    }

    public long getGpCount() {
        return stubCount + gridCount;
    }


    public Double getProfit() {
        return Double.parseDouble(String.format("%.2f", stubProfit + gridProfit));
    }

}
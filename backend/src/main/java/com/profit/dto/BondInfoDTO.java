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
     * 短线持股数量
     */
    public long stubCount;
    /**
     * 网格持股数量
     */
    public long gridCount;
    /**
     * 持股盈亏
     */
    public Double gpProfit;
    /**
     * 当前总盈亏
     */
    public Double curProfit;
    /**
     * 当前总盈亏
     */
    public Double profit;

    public Double getCurProfit() {
        return Double.parseDouble(String.format("%.2f", stubProfit + gridProfit + gpProfit));
    }

    public Double getProfit() {
        return Double.parseDouble(String.format("%.2f", stubProfit + gridProfit));
    }

}
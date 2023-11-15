package com.profit.dto;

import com.profit.base.domain.BondStatistics;
import lombok.Data;


@Data
public class BondStatisticsDTO extends BondStatistics {

    /**
     * 持股盈亏
     */
    private Double stockProfit;

    /**
     * 融资市值
     */
    private Double liability;

    /**
     * 利息
     */
    private Double interest;
}
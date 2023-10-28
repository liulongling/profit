package com.profit.dto;

import com.profit.base.domain.BondStatistics;
import lombok.Data;


@Data
public class BondStatisticsDTO extends BondStatistics {

    /**
     * 持股盈亏
     */
    private Double stockProfit;

}
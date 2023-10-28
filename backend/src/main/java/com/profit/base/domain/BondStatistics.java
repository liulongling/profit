package com.profit.base.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
public class BondStatistics {
    /**
     * 主键
     */
    private Long id;

    /**
     * 持股市值
     */
    private Double stock;

    /**
     * 现金
     */
    private Double ready;

    /**
     * 冻结费用
     */
    private Double freeze;

    /**
     * 仓位占比
     */
    private Double position;

    /**
     * 盈利笔数
     */
    private Integer profitNumber;

    /**
     * 亏损笔数
     */
    private Integer lossNumber;

    /**
     * 胜率
     */
    private String winning;

    /**
     * 总收益
     */
    private Double profit;

    /**
     * 购买总金额
     */
    private Double buyMoney;

    /**
     * 出售总金额
     */
    private Double sellMoney;

    /**
     * 止损金额
     */
    private Double lossMoney;

    /**
     * 消耗总佣金
     */
    private Double cost;

    /**
     * 
     */
    private Date updateTime;
}
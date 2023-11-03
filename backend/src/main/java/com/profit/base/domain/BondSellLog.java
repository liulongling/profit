package com.profit.base.domain;

import lombok.Data;

import java.util.Date;

@Data
public class BondSellLog {
    /**
     * 唯一ID
     */
    private Long id;

    /**
     *
     */
    private String gpId;

    /**
     * 预冻结资金
     */
    private Double freeze;

    /**
     * 利息
     */
    private Double interest;

    /**
     *
     */
    private Long buyId;

    /**
     * 出售价格
     */
    private Double price;

    /**
     * 出售数量
     */
    private Integer count;

    /**
     * 出售总价格
     */
    private Double totalPrice;

    /**
     * 总佣金税费，包含购买佣金
     */
    private Double totalCost;

    /**
     * 佣金
     */
    private Double cost;

    /**
     * 收益
     */
    private Double income;

    /**
     * 出售后当前剩余数量
     */
    private Integer surplusCount;

    /**
     * 出售前现金
     */
    private Double realyBefore;


    /**
     * 出售后现金
     */
    private Double realyAfter;

    /**
     * 操作时间
     */
    private Date createTime;
}
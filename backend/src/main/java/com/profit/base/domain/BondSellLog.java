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
     * 操作时间
     */
    private Date createTime;
}
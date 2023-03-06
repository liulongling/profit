package com.profit.base.domain;

import java.util.Date;

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
     * 佣金
     */
    private Double cost;

    /**
     * 收益
     */
    private Double income;

    /**
     * 操作时间
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGpId() {
        return gpId;
    }

    public void setGpId(String gpId) {
        this.gpId = gpId == null ? null : gpId.trim();
    }

    public Long getBuyId() {
        return buyId;
    }

    public void setBuyId(Long buyId) {
        this.buyId = buyId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
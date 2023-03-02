package com.profit.base.domain;

import java.util.Date;

public class BondBuyLog {
    /**
     * 唯一ID
     */
    private Long id;

    /**
     * 股票代码
     */
    private String gpId;

    /**
     * 购买价格
     */
    private Double price;

    /**
     * 购买数量
     */
    private Integer count;

    /**
     * 税费
     */
    private Double cost;

    /**
     * type:0 网格 type:1短线
     */
    private Byte type;

    /**
     * 已出售数量
     */
    private Integer sellCount;

    /**
     * 出售收益
     */
    private Double sellIncome;

    /**
     * 购买日期
     */
    private String buyDate;

    /**
     * 0:未售完 1:已售完
     */
    private Byte status;

    /**
     * 操作时间
     */
    private Date operTime;

    /**
     * 创建时间
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

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Integer getSellCount() {
        return sellCount;
    }

    public void setSellCount(Integer sellCount) {
        this.sellCount = sellCount;
    }

    public Double getSellIncome() {
        return sellIncome;
    }

    public void setSellIncome(Double sellIncome) {
        this.sellIncome = sellIncome;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate == null ? null : buyDate.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
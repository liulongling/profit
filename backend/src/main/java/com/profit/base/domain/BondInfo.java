package com.profit.base.domain;

import java.util.Date;

public class BondInfo {
    /**
     * 主键
     */
    private String id;

    /**
     * 股票名称
     */
    private String name;

    /**
     * 当前股价
     */
    private Double price;

    /**
     * 上市所属板块 0：上证 1:深圳 2：创业板 3：etf
     */
    private String plate;

    /**
     * 是否ETF
     */
    private Byte isEtf;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate == null ? null : plate.trim();
    }

    public Byte getIsEtf() {
        return isEtf;
    }

    public void setIsEtf(Byte isEtf) {
        this.isEtf = isEtf;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
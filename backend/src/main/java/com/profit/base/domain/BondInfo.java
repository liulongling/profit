package com.profit.base.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
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
     * 昨日收盘价
     */
    private Double oldPrice;

    /**
     * 上市所属板块 0：上证 1:深圳 2：创业板 3：etf
     */
    private String plate;

    /**
     * 是否ETF 1：etf
     */
    private Byte isEtf;

    /**
     * 仓位占比
     */
    private Double position;

    /**
     * 0:上架 -1：下架
     */
    private Byte status;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;
}
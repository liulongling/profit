package com.profit.base.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.profit.commons.utils.BondUtils;
import com.profit.commons.utils.DateUtils;
import lombok.Data;

import java.util.Date;

@Data
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
     * 购买总价格
     */
    private Double totalPrice;

    /**
     * 融资归还金额
     */
    private Double backMoney;

    /**
     * 融资归还时间
     */
    private Date backTime;

    /**
     * 融资利息
     */
    private Double interest;

    /**
     * 购买消耗佣金
     */
    private Double buyCost;

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
     * 备注
     */
    private String remarks;

    /**
     * 是否融资买入 0:不是 1：是
     */
    private Byte financing;

    /**
     * 创建时间
     */
    private Date createTime;

    @JSONField(serialize = false, deserialize = false)
    public void addInterest(Double intersert) {
        this.interest += intersert;
    }

    @JSONField(serialize = false, deserialize = false)
    public void addRemarks(Double backMoney, Double intersert) {
        remarks += DateUtils.getDateString(backTime, DateUtils.PATTERN_DATE) + "归还金额" + backMoney + "利息:" + intersert + ";";
    }

    @JSONField(serialize = false, deserialize = false)
    public double countInterest() {
        double interest = this.interest;
        if (financing == 1) {
            Date lendDate;
            if (backTime != null) {
                lendDate = backTime;
            } else {
                lendDate = DateUtils.string2Date(buyDate, DateUtils.DATE_PATTERM);
            }
            Double lendMoney = (count - sellCount * price - backMoney) + buyCost;
            interest += BondUtils.countInterest(lendMoney, lendDate);
        }

        return interest;
    }

    @JSONField(serialize = false, deserialize = false)
    public double notbackInterest() {
        if (financing == 1) {
            Date lendDate;
            if (backTime != null) {
                lendDate = backTime;
            } else {
                lendDate = DateUtils.string2Date(buyDate, DateUtils.DATE_PATTERM);
            }
            Double lendMoney = (count - sellCount * price - backMoney) + buyCost;
            return BondUtils.countInterest(lendMoney, lendDate);
        }
        return 0;
    }
}
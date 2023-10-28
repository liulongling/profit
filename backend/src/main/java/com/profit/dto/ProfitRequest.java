package com.profit.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProfitRequest {
    /**
     * type 1 : 单只股票每天收益 2：每月总收益 3:每天总收益
     */
    private int type;
    /**
     * 股票ID
     */
    private String gpId;

}

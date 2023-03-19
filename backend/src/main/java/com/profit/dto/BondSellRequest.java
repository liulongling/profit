package com.profit.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BondSellRequest {
    private Long buyId;
    private String startTime;
    private String endTime;
    private int type;
}

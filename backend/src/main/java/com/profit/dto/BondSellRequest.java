package com.profit.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BondSellRequest {
    private Date startTime;
    private Date endTime;
}

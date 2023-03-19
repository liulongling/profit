package com.profit.dto;

import com.profit.base.domain.BondSellLog;
import lombok.Data;

@Data
public class BondSellDTO extends BondSellLog {
    private Long totalPrice;

}

package com.profit.dto;

import com.profit.base.domain.BondBuyLog;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BondBuyRequest extends BondBuyLog {
    private Double sellPrice;
}

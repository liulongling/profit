package com.profit.dto;

import com.profit.base.domain.BondBuyLog;
import lombok.Data;

@Data
public class BondBuyLogDTO extends BondBuyLog {
    public String name;
    public String sellAvgPrice;
    public String curIncome;
    public String typeName;

    public String getTypeName() {
        return getType() == 1 ? "短线" : "网格";
    }
}
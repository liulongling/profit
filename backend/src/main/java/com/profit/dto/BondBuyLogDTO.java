package com.profit.dto;

import com.profit.base.domain.BondBuyLog;
import lombok.Data;

@Data
public class BondBuyLogDTO extends BondBuyLog {
    public String name;
    public String sellAvgPrice;
    public String curIncome;
    public String typeName;
    public String girdSpacing;
    public String sellDate;
    public Double income;
    public Double curPrice;

    public String getTypeName() {
        return getType() == 1 ? "短线" : "长线";
    }
}
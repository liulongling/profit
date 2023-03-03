package com.profit.commons.utils;


import com.profit.base.domain.BondInfo;

public class BondUtils {

    public static Double getTaxation(BondInfo bondInfo, Double total, boolean isSell) {
        Double taxation = 0.000;
        if(!bondInfo.getName().contains("ETF")){
            if (bondInfo.getPlate().equals("sh")) {
                //过户费成交金额0.002%
                taxation += total * 0.00002;
            }
            if (isSell) {
                //印花税0.1%
                taxation += total * 0.001;
            }
        }
        //券商佣金
        double zqyj= total * 0.0001;
        if(zqyj < 1){
            zqyj = 1;
        }
        taxation += zqyj;
        return Double.parseDouble(String.format("%.3f", taxation));

    }
}

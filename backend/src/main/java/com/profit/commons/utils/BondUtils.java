package com.profit.commons.utils;


public class BondUtils {

    public static Double getTaxation(String plate, Double total, boolean isSell) {
        Double taxation = 0.000;
        if (plate.equals("上证")) {
            //过户费成交金额0.002%
            taxation += total * 0.00002;
        }
        if (isSell) {
            //印花税0.1%
            taxation += total * 0.001;
        }
        //券商佣金
        double zqyj= total * 0.00001;
        if(zqyj < 1){
            zqyj = 1;
        }
        taxation += zqyj;
        return Double.parseDouble(String.format("%.3f", taxation));

    }
}

package com.profit.commons.utils;


public class BondUtils {

    /**
     * 计算佣金
     *
     * @param isETF  是否ETF
     * @param plate  股票所属板块
     * @param total  交易金额
     * @param isSell 是否出售
     * @return
     */
    public static Double getTaxation(boolean isETF, String plate, Double total, boolean isSell) {
        Double taxation = 0.000;
        if (plate.equals("hk")) {
            //佣金万一
            taxation += total * 0.0001;
            if (isSell) {
                //过户费成交金额0.002%
                taxation += total * 0.00002;
            } else {

            }

        } else if ((plate.equals("sh") || plate.equals("sz")) && !isETF) {
            if (plate.equals("sh")) {
                //上海过户费成交金额0.002%
                taxation += total * 0.00002;
            }
            if (isSell) {
                //印花税0.1%
                taxation += total * 0.001;
            }
        }
        //券商佣金
        double zqyj = total * 0.0001;

        taxation += zqyj;
        return Double.parseDouble(String.format("%.3f", taxation));

    }
}

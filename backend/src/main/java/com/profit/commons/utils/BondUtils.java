package com.profit.commons.utils;


import com.profit.base.domain.BondInfo;
import com.profit.commons.constants.BondConstants;

public class BondUtils {

    /**
     * 计算佣金
     *
     * @param bondInfo
     * @param total    交易金额
     * @param isSell   是否出售
     * @return
     */
    public static Double getTaxation(BondInfo bondInfo, Double total, boolean isSell) {
        Double taxation = 0.00;
        if (bondInfo.getId().equals(BondConstants.NHG_CODE)) {
            //券商佣金
            if(!isSell){
                taxation += total * 0.00001;
            }
        } else if (bondInfo.getPlate().equals("hk")) {
            //佣金万一
            taxation += total * 0.0001;
            if (isSell) {
                //过户费成交金额0.002%
                taxation += total * 0.00002;
            }
        } else if (bondInfo.getIsEtf() == 1) {
            //券商佣金
            taxation += total * 0.0001;
        } else if ((bondInfo.getPlate().equals("sh") || bondInfo.getPlate().equals("sz"))) {
            if (bondInfo.getIsEtf() != 1) {
                //过户费成交金额0.001%
                taxation += total * 0.00001;
                if (isSell) {
                    //印花税0.1%
                    taxation += total * 0.001;
                }
                //券商佣金
                taxation += total * 0.0001;
            }
        }
        return Double.parseDouble(String.format("%.2f", taxation));

    }
}

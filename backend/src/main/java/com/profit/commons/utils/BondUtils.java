package com.profit.commons.utils;


import com.profit.base.domain.BondInfo;
import com.profit.commons.constants.BondConstants;

import java.util.Date;

public class BondUtils {

    public static String getBaseId(String id) {
        if (id.equals("1-600036")) {
            return "600036";
        } else if (id.equals("1-002142")) {
            return "002142";
        } else if (id.equals("1-512480")) {
            return "512480";
        } else if (id.equals("1-515790")) {
            return "515790";
        } else if (id.equals("1-600340")) {
            return "600340";
        } else {
            return id;
        }
    }

    /**
     * 计算利息
     *
     * @param lendMoney 借出 金额
     * @param lendDate  借出时间
     * @return
     */
    public static Double countInterest(Double lendMoney, Date lendDate) {
        long lendDay = DateUtils.compareDateInterval(lendDate, new Date());
        return Double.parseDouble(String.format("%.2f", lendMoney * (0.05 / 360) * lendDay));

    }

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
            if (!isSell) {
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
                    //印花税0.1% --> 0.05% 20230827印花税减半
                    taxation += total * 0.0005;
                }
                //券商佣金
                taxation += total * 0.0001;
            }
        }
        return Double.parseDouble(String.format("%.2f", taxation));

    }
}

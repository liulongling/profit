package com.profit.comparator;

import com.profit.dto.BondBuyLogDTO;

import java.util.Comparator;

/**
 * 收益排序
 *
 * @Author:liulongling
 * @Date:2022/4/27 10:50
 */
public class ComparatorIncome implements Comparator {

    public ComparatorIncome(){
    }

    @Override
    public int compare(Object o1, Object o2) {
        BondBuyLogDTO className1=(BondBuyLogDTO)o1;
        BondBuyLogDTO className2=(BondBuyLogDTO)o2;
        if(className1.getIncome() < className2.getIncome()){
            return 1;
        }
        return -1;
    }
}

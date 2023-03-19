package com.profit.service;

import com.profit.base.domain.*;
import com.profit.base.mapper.BondBuyLogMapper;
import com.profit.base.mapper.BondInfoMapper;
import com.profit.base.mapper.BondSellLogMapper;
import com.profit.commons.utils.BeanUtils;
import com.profit.commons.utils.BondUtils;
import com.profit.commons.utils.DateUtils;
import com.profit.commons.utils.HttpUtil;
import com.profit.dto.BondBuyLogDTO;
import com.profit.dto.BondSellRequest;
import com.profit.dto.ProfitDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class BondService {
    @Resource
    private RestTemplate restTemplate;
    @Value("${gtimg.server-url}")
    private String serverUrl;
    @Resource
    private BondInfoMapper bondInfoMapper;
    @Resource
    private BondSellLogMapper bondSellLogMapper;
    @Resource
    private BondBuyLogMapper bondBuyLogMapper;


    /**
     * 卖出均价 = (卖出数量*买入价格+ 收益) / 卖出数量
     * @return
     */
    public void loadSellAvgPrice(BondBuyLog bondBuyLog, BondBuyLogDTO buyLogDTO ){
        if(bondBuyLog.getSellCount() <= 0){
            return ;
        }
        //卖出均价= (卖出数量*买入价格+ 收益) / 卖出数量
        double sellAvgPrice = (bondBuyLog.getPrice() * bondBuyLog.getSellCount() + bondBuyLog.getSellIncome() + bondBuyLog.getCost()) / bondBuyLog.getSellCount();
        Double avg = Double.parseDouble(String.format("%.3f", sellAvgPrice));
        buyLogDTO.setSellAvgPrice(avg + "(" + String.format("%.2f", ((avg - bondBuyLog.getPrice()) / avg) * 100) + "%)");
        BondSellLogExample bondSellLogExample = new BondSellLogExample();
        bondSellLogExample.createCriteria().andBuyIdEqualTo(buyLogDTO.getId());
        bondSellLogExample.setOrderByClause("create_time desc limit 0,1");
        List<BondSellLog> bondSellLogs = bondSellLogMapper.selectByExample(bondSellLogExample);
        if (bondSellLogs != null) {
            try {
                buyLogDTO.setSellDate(DateUtils.getDateString(bondSellLogs.get(0).getCreateTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当前持股盈亏
     * @return
     */
    public void loadCurBondIncome(BondInfo bondInfo,BondBuyLog bondBuyLog, BondBuyLogDTO buyLogDTO ){
        //当前持股盈亏
        if (bondBuyLog.getCount() > bondBuyLog.getSellCount()) {
            int surplusCount = bondBuyLog.getCount() - bondBuyLog.getSellCount();
            Double curIncome = bondInfo.getPrice() * surplusCount - bondBuyLog.getPrice() * surplusCount;
            curIncome -= BondUtils.getTaxation(bondInfo.getIsEtf() == 1, bondInfo.getPlate(), surplusCount * bondInfo.getPrice(), true);
            curIncome -= BondUtils.getTaxation(bondInfo.getIsEtf() == 1, bondInfo.getPlate(), surplusCount * bondBuyLog.getPrice(), false);
            //涨跌幅
            Double zdf = Double.parseDouble(String.format("%.2f", (((bondInfo.getPrice() - bondBuyLog.getPrice()) / bondInfo.getPrice()) * 100)));
            buyLogDTO.setIncome(curIncome);
            buyLogDTO.setCurIncome(Double.parseDouble(String.format("%.3f", curIncome)) + "(" + zdf + "%)");
        }
    }


    /**
     * 加载收益数据
     * @param map
     * @param bondSellRequest
     */
    public void loadProfitDTOData(Map<String, ProfitDTO> map, BondSellRequest bondSellRequest) {
        List<Map<Object, Object>> profitList = bondSellLogMapper.listGroupByDate(bondSellRequest);
        Map<Object, Object> profitMap = BeanUtils.list2Map(profitList, "date", "income");
        for (Object object : profitMap.keySet()) {
            ProfitDTO profitDTO = map.get(object);
            if (profitDTO == null || !map.containsKey(object)) {
                profitDTO = new ProfitDTO();
                profitDTO.setDate(object.toString());
            }
            Double profit = Double.parseDouble(String.format("%.3f", profitMap.get(object)));
            if (bondSellRequest.getType() == 0) {
                profitDTO.setGridProfit(profit);
            } else if (bondSellRequest.getType() == 1) {
                profitDTO.setStubProfit(profit);
            }
            profitDTO.setTotalProfit(Double.parseDouble(String.format("%.3f", profitDTO.getGridProfit() + profitDTO.getStubProfit())));
            map.put(object.toString(), profitDTO);
        }
    }

    /**
     * 查询股票数量
     *
     * @param bondInfo
     * @param type     0：网格 1：短线
     * @return
     */
    public Long getBondNumber(BondInfo bondInfo, byte type) {
        long buyCount = 0, sellCount = 0;
        Map<Object, Object> map = bondBuyLogMapper.sumBuySellCount(bondInfo.getId(), type);
        if (map != null) {
            for (Object o : map.keySet()) {
                if (o.equals("buyCount")) {
                    buyCount = Long.valueOf(map.get(o).toString());
                } else if (o.equals("sellCount")) {
                    sellCount = Long.valueOf(map.get(o).toString());
                }
            }
        }
        return buyCount - sellCount;
    }

    /**
     * 查询指定范围内收益
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public Map<Object, Object> getProfitByDate(String startTime, String endTime) {
        BondSellRequest bondSellRequest = new BondSellRequest();
        bondSellRequest.setStartTime(startTime);
        bondSellRequest.setEndTime(endTime);
        List<Map<Object, Object>> profitList = bondSellLogMapper.listGroupByGpId(bondSellRequest);
        return BeanUtils.list2Map(profitList, "gpId", "income");
    }

    public void refurbish() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 9 && hour < 15) {
            List<BondInfo> list = bondInfoMapper.selectByExample(new BondInfoExample());
            for (BondInfo bondInfo : list) {
                Map<String, String> uriMap = new HashMap<>();
                if (bondInfo.getId().equals("131810")) {
                    continue;
                }
                uriMap.put("q", bondInfo.getPlate() + bondInfo.getId());
                ResponseEntity responseEntity = restTemplate.getForEntity
                        (
                                HttpUtil.generateRequestParameters(serverUrl, uriMap),
                                String.class
                        );

                if (responseEntity != null) {
                    String reslut = (String) responseEntity.getBody();
                    String[] str = reslut.split("~");
                    if (str != null) {
                        bondInfo.setPrice(Double.valueOf(str[3]));
                        bondInfoMapper.updateByPrimaryKey(bondInfo);
                    }
                }

            }
        }

    }
}

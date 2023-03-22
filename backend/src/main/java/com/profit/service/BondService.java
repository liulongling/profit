package com.profit.service;

import com.profit.base.domain.*;
import com.profit.base.mapper.BondBuyLogMapper;
import com.profit.base.mapper.BondInfoMapper;
import com.profit.base.mapper.BondSellLogMapper;
import com.profit.commons.utils.*;
import com.profit.dto.*;
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
     * 股票今天数据
     *
     * @param gpId null 查所有股票
     * @return
     */
    public TodayTaxationDTO loadToadyTaxationDTO(String gpId) {
        TodayTaxationDTO todayTaxationDTO = new TodayTaxationDTO();
        //今日出售总收益
        Map<Object, Object> todayProfitMap = getProfitByDate(DateUtils.getTimeString(DateUtils.getTime(new Date(), 0, 0, 0)), DateUtils.getTimeString(DateUtils.getTime(new Date(), 23, 59, 59)));
        Double profit = 0.00;
        if (todayProfitMap != null) {
            for (Object key : todayProfitMap.keySet()) {
                if (gpId == null) {
                    profit += Double.parseDouble(String.format("%.2f", todayProfitMap.get(key)));
                } else {
                    if (key.equals(gpId)) {
                        profit = Double.parseDouble(String.format("%.2f", todayProfitMap.get(key)));
                        break;
                    }
                }
            }
        }
        todayTaxationDTO.setTodayProfit(Double.parseDouble(String.format("%.2f", profit)));

        List<BondBuyLog> buyLogs = getBondBuyLogs(gpId, DateUtils.getDateString(new Date(), DateUtils.DATE_PATTERM));
        Double buyAmount = 0.0;
        int transactionNumber = 0;
        if (buyLogs != null) {
            for (BondBuyLog bondBuyLog : buyLogs) {
                buyAmount += bondBuyLog.getPrice() * bondBuyLog.getCount();
                transactionNumber++;
            }
        }
        todayTaxationDTO.setBuyAmount(buyAmount);

        List<BondSellLog> sellLogs = getBondSellLogs(gpId, DateUtils.getTime(new Date(), 0, 0, 0), DateUtils.getTime(new Date(), 23, 59, 59));
        Double sellAmount = 0.0;
        Double cost = 0.0;
        Double maxProfit = 0.0;
        Double maxLoss = 0.0;
        if (buyLogs != null) {
            for (BondSellLog bondSellLog : sellLogs) {
                if (bondSellLog.getIncome() > 0) {
                    todayTaxationDTO.incrProfitNumber();
                } else {
                    todayTaxationDTO.incrPlossNumber();
                }
                if (bondSellLog.getIncome() > maxProfit) {
                    maxProfit = bondSellLog.getIncome();
                }
                if (bondSellLog.getIncome() < maxLoss) {
                    maxLoss = bondSellLog.getIncome();
                }
                sellAmount += bondSellLog.getPrice() * bondSellLog.getCount();
                cost += bondSellLog.getCost();
                transactionNumber++;
            }
        }
        todayTaxationDTO.setSellAmount(sellAmount);
        todayTaxationDTO.setCost(Double.parseDouble(String.format("%.2f", cost)));
        todayTaxationDTO.setTransactionNumber(transactionNumber);
        todayTaxationDTO.setMaxProfit(maxProfit);
        todayTaxationDTO.setMaxLoss(maxLoss);
        todayTaxationDTO.setTransactionAmount(Double.parseDouble(String.format("%.2f", buyAmount + sellAmount)));
        if (todayTaxationDTO.getTotalNumber() > 0) {
            todayTaxationDTO.setWinning(StringUtil.pencent(todayTaxationDTO.getProfitNumber(), todayTaxationDTO.getTotalNumber()));
        }
        return todayTaxationDTO;
    }

    /**
     * 加载股票信息
     *
     * @param bondInfo
     * @return
     */
    public BondInfoDTO loadBondInfoDTO(BondInfo bondInfo) {
        BondInfoDTO bondInfoDTO = BeanUtils.copyBean(new BondInfoDTO(), bondInfo);
        Double stubProfit = bondBuyLogMapper.sumSellIncome(bondInfo.getId(), (byte) 1);
        if (stubProfit == null) stubProfit = 0.00;
        bondInfoDTO.setStubProfit(Double.parseDouble(String.format("%.2f", stubProfit)));
        Double gridProfit = bondBuyLogMapper.sumSellIncome(bondInfo.getId(), (byte) 0);
        if (gridProfit == null) gridProfit = 0.00;
        bondInfoDTO.setGridProfit(Double.parseDouble(String.format("%.2f", gridProfit)));

        bondInfoDTO.setStubCount(getBondNumber(bondInfo, (byte) 1));
        bondInfoDTO.setGridCount(getBondNumber(bondInfo, (byte) 0));

        //当前持股盈亏
        BondBuyLogExample bondBuyLogExample = new BondBuyLogExample();
        BondBuyLogExample.Criteria criteria = bondBuyLogExample.createCriteria();
        criteria.andGpIdEqualTo(bondInfo.getId());
        criteria.andStatusEqualTo((byte) 0);
        List<BondBuyLog> bondBuyLogs = bondBuyLogMapper.selectByExample(bondBuyLogExample);
        if (bondBuyLogs != null) {
            Double total = 0.00;
            for (BondBuyLog bondBuyLog : bondBuyLogs) {
                if (bondBuyLog.getCount() > bondBuyLog.getSellCount()) {
                    int surplusCount = bondBuyLog.getCount() - bondBuyLog.getSellCount();
                    total += (bondInfo.getPrice() * surplusCount) - (bondBuyLog.getPrice() * surplusCount);
                }
            }
            bondInfoDTO.setGpProfit(Double.parseDouble(String.format("%.2f", total)));
        }
        return bondInfoDTO;
    }


    public Map<String, ProfitDTO> totalProfit() {
        BondSellRequest bondSellRequest = new BondSellRequest();
        bondSellRequest.setType(0);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        bondSellRequest.setStartTime(DateUtils.getTimeString(DateUtils.getBeginTime(year, 1)));
        bondSellRequest.setEndTime(DateUtils.getTimeString(DateUtils.getBeginTime(year, 12)));

        Map<String, ProfitDTO> map = new HashMap<>();

        loadProfitDTOData(map, bondSellRequest);
        bondSellRequest.setType(1);
        loadProfitDTOData(map, bondSellRequest);
        return map;
    }

    /**
     * 查询指定时间购买记录
     *
     * @param date
     * @return
     */
    public List<BondBuyLog> getBondBuyLogs(String gpId, String date) {
        BondBuyLogExample bondBuyLogExample = new BondBuyLogExample();
        BondBuyLogExample.Criteria criteria = bondBuyLogExample.createCriteria();
        criteria.andBuyDateEqualTo(date);
        if (gpId != null) {
            criteria.andGpIdEqualTo(gpId);
        }
        return bondBuyLogMapper.selectByExample(bondBuyLogExample);
    }


    /**
     * 查询时间范围内股票出售记录
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return
     */
    public List<BondSellLog> getBondSellLogs(String gpId, Date startDate, Date endDate) {
        BondSellLogExample bondSellLogExample = new BondSellLogExample();
        BondSellLogExample.Criteria criteria = bondSellLogExample.createCriteria();
        criteria.andCreateTimeBetween(startDate, endDate);
        if (gpId != null) {
            criteria.andGpIdEqualTo(gpId);
        }
        bondSellLogExample.createCriteria().andCreateTimeBetween(startDate, endDate);
        return bondSellLogMapper.selectByExample(bondSellLogExample);
    }


    /**
     * 卖出均价 = (卖出数量*买入价格+ 收益) / 卖出数量
     *
     * @param bondBuyLog
     * @param buyLogDTO
     */
    public void loadSellAvgPrice(BondBuyLog bondBuyLog, BondBuyLogDTO buyLogDTO) {
        if (bondBuyLog.getSellCount() <= 0) {
            return;
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
     *
     * @param bondInfo
     * @param bondBuyLog
     * @param buyLogDTO
     */
    public void loadCurBondIncome(BondInfo bondInfo, BondBuyLog bondBuyLog, BondBuyLogDTO buyLogDTO) {
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
     *
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
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public Map<Object, Object> getProfitByDate(String startTime, String endTime) {
        BondSellRequest bondSellRequest = new BondSellRequest();
        bondSellRequest.setStartTime(startTime);
        bondSellRequest.setEndTime(endTime);
        List<Map<Object, Object>> profitList = bondSellLogMapper.listGroupByGpId(bondSellRequest);
        return BeanUtils.list2Map(profitList, "gpId", "income");
    }

    /**
     * 更新股价
     */
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

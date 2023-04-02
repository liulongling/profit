package com.profit.service;

import com.profit.base.domain.*;
import com.profit.base.mapper.*;
import com.profit.commons.constants.BondConstants;
import com.profit.commons.utils.*;
import com.profit.dto.*;
import org.apache.commons.lang.BooleanUtils;
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
    @Resource
    private BondStatisticsMapper bondStatisticsMapper;
    @Resource
    private BaseDataMapper baseDataMapper;

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
        int buyNumber = 0;
        if (buyLogs != null) {
            for (BondBuyLog bondBuyLog : buyLogs) {
                buyAmount += bondBuyLog.getPrice() * bondBuyLog.getCount();
                buyNumber++;
            }
        }
        todayTaxationDTO.setBuyAmount(Double.parseDouble(String.format("%.2f", buyAmount)));

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
            }
        }
        todayTaxationDTO.setSellAmount(Double.parseDouble(String.format("%.2f", sellAmount)));
        todayTaxationDTO.setCost(Double.parseDouble(String.format("%.2f", cost)));
        todayTaxationDTO.setBuyNumber(buyNumber);
        todayTaxationDTO.setMaxProfit(maxProfit);
        todayTaxationDTO.setMaxLoss(maxLoss);
        todayTaxationDTO.setTransactionAmount(Double.parseDouble(String.format("%.2f", buyAmount + sellAmount)));
        if (todayTaxationDTO.getTotalNumber() > 0) {
            todayTaxationDTO.setWinning(StringUtil.pencentWin(todayTaxationDTO.getProfitNumber(), todayTaxationDTO.getTotalNumber()));
        }
        return todayTaxationDTO;
    }


    public TotalProfitDTO loadTotalProfitDTO() {
        TotalProfitDTO totalProfitDTO = new TotalProfitDTO();
        BondSellLogExample bondSellLogExample = new BondSellLogExample();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        bondSellLogExample.createCriteria().andGpIdNotEqualTo("131810").andIncomeGreaterThan(0.0).andCreateTimeBetween(DateUtils.getBeginTime(year, 1), DateUtils.getEndTime(year, 12));
        Long totalProfitNumber = bondSellLogMapper.countByExample(bondSellLogExample);
        totalProfitDTO.setTotalProfitNumber(totalProfitNumber.intValue());

        bondSellLogExample = new BondSellLogExample();
        bondSellLogExample.createCriteria().andGpIdNotEqualTo("131810").andIncomeLessThan(0.0).andCreateTimeBetween(DateUtils.getBeginTime(year, 1), DateUtils.getEndTime(year, 12));
        long totalLossNumber = bondSellLogMapper.countByExample(bondSellLogExample);
        totalProfitDTO.setTotalLossNumber(totalLossNumber);
        if (totalProfitNumber + totalLossNumber > 0) {
            //胜率=获胜场次÷总比赛场次x100%
            totalProfitDTO.setAvgWinning(StringUtil.pencentWin(totalProfitNumber, totalProfitNumber + totalLossNumber));
        }

        BondSellRequest bondSellRequest = new BondSellRequest();
        bondSellRequest.setStartTime(DateUtils.getTimeString(DateUtils.getBeginTime(year, 1)));
        bondSellRequest.setEndTime(DateUtils.getTimeString(DateUtils.getEndTime(year, 12)));

        Double totalProfit = bondSellLogMapper.sumIncome(bondSellRequest);
        totalProfitDTO.setTotalProfit(Double.parseDouble(String.format("%.2f", totalProfit)));

        List<BondInfo> bondInfos = bondInfoMapper.selectByExample(new BondInfoExample());
        Double stockValue = 0.0;
        for (BondInfo bondInfo : bondInfos) {
            BondInfoDTO bondInfoDTO = loadBondInfoDTO(bondInfo);
            stockValue += bondInfoDTO.getMarket();
        }

        totalProfitDTO.setStockValue(Double.parseDouble(String.format("%.2f", stockValue)));
        totalProfitDTO.setLossMoney(Double.parseDouble(String.format("%.2f", bondSellLogMapper.sumLossIncome(bondSellRequest))));
        totalProfitDTO.setCost(Double.parseDouble(String.format("%.2f", bondSellLogMapper.sumCost(bondSellRequest))));
        return totalProfitDTO;
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

        bondInfoDTO.setStubCount(getBondNumber(bondInfo, BondConstants.SHORT_LINE));
        bondInfoDTO.setGridCount(getBondNumber(bondInfo, BondConstants.LONG_LINE));
        bondInfoDTO.setSuperStubCount(getBondNumber(bondInfo, BondConstants.SUPER_SHORT_LINE));

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

        //计算成本价 = (当前价格 * 股票数量 - 总盈亏) /数量
        Double costPrice = (bondInfo.getPrice() * bondInfoDTO.getGpCount() - bondInfoDTO.getCurProfit()) / bondInfoDTO.getGpCount();
        bondInfoDTO.setCostPrice(Double.parseDouble(String.format("%.3f", costPrice)));

        //计算胜率
        BondSellLogExample bondSellLogExample = new BondSellLogExample();
        bondSellLogExample.createCriteria().andGpIdEqualTo(bondInfo.getId()).andIncomeGreaterThan(0.0);
        long winCount = bondSellLogMapper.countByExample(bondSellLogExample);
        bondSellLogExample = new BondSellLogExample();
        bondSellLogExample.createCriteria().andGpIdEqualTo(bondInfo.getId());
        long totalCount = bondSellLogMapper.countByExample(bondSellLogExample);
        bondInfoDTO.setWinning(StringUtil.pencentWin(winCount, totalCount));

        bondBuyLogExample = new BondBuyLogExample();
        bondBuyLogExample.createCriteria().andGpIdEqualTo(bondInfo.getId()).andStatusEqualTo(BondConstants.WAIT_BUY);
        List<BondBuyLog> waitBuyBondBuyLogS = bondBuyLogMapper.selectByExample(bondBuyLogExample);
        for (BondBuyLog bondBuyLog : waitBuyBondBuyLogS) {
            if (bondBuyLog != null) {
                if (bondBuyLog.getPrice() >= bondInfo.getPrice()) {
                    bondInfoDTO.setWaitBuy(true);
                    break;
                }
            }
        }

        bondInfoDTO.setMarket(Double.parseDouble(String.format("%.0f", bondInfo.getPrice() * bondInfoDTO.getGpCount())));
        BondStatistics bondStatistics = bondStatisticsMapper.selectByPrimaryKey(1L);
        bondInfoDTO.setRealPosition(Double.parseDouble(String.format("%.2f", bondInfoDTO.getMarket() / (bondInfoDTO.getMarket() + bondStatistics.getReady()) * 10)));
        bondInfoDTO.setPosition(bondInfo.getPosition());
        return bondInfoDTO;
    }


    /**
     * 总收益
     *
     * @return
     */
    public ProfitDTO totalProfit() {
        ProfitDTO profitDTO = new ProfitDTO();
        for (int i = 1; i <= 12; i++) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            String month = i < 10 ? ("0" + i) : String.valueOf(i);
            profitDTO.getDate().add(year + month);
            BondSellRequest bondSellRequest = new BondSellRequest();
            bondSellRequest.setType(0);
            bondSellRequest.setStartTime(DateUtils.getFirstDayOfMonth(i));
            bondSellRequest.setEndTime(DateUtils.getLastDayOfMonth(i));
            Double gridProfit = Double.parseDouble(String.format("%.2f", bondSellLogMapper.sumIncomeByType(bondSellRequest)));
            profitDTO.getGridProfit().add(gridProfit);
            bondSellRequest.setType(1);
            Double stubProfit = Double.parseDouble(String.format("%.2f", bondSellLogMapper.sumIncomeByType(bondSellRequest)));
            profitDTO.getStubProfit().add(stubProfit);
            profitDTO.getTotalProfit().add(Double.parseDouble(String.format("%.2f", gridProfit + stubProfit)));

            Double lossProfit = Double.parseDouble(String.format("%.2f", bondSellLogMapper.sumLossIncome(bondSellRequest)));
            profitDTO.getLossProfit().add(lossProfit);

            Double cost = Double.parseDouble(String.format("%.2f", bondSellLogMapper.sumCost(bondSellRequest)));
            profitDTO.getCost().add(cost);
        }
        return profitDTO;
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
        criteria.andStatusBetween(BondConstants.NOT_SELL, BondConstants.SOLD);
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
            curIncome -= BondUtils.getTaxation(bondInfo, surplusCount * bondInfo.getPrice(), true);
            curIncome -= BondUtils.getTaxation(bondInfo, surplusCount * bondBuyLog.getPrice(), false);
            //涨跌幅
            Double zdf = Double.parseDouble(String.format("%.2f", (((bondInfo.getPrice() - bondBuyLog.getPrice()) / bondInfo.getPrice()) * 100)));
            buyLogDTO.setIncome(curIncome);
            buyLogDTO.setCurIncome(Double.parseDouble(String.format("%.2f", curIncome)) + "(" + zdf + "%)");
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
    public void refurbishBondPrice() {
        String date = DateUtils.getDateString(new Date(), DateUtils.DATE_PATTERM);
        Map<String, Object> map = WeekdayUtil.isWeekday(date);
        if (map != null && BooleanUtils.toBoolean(map.get("isWeekDay").toString())) {
            List<BondInfo> list = bondInfoMapper.selectByExample(new BondInfoExample());
            for (BondInfo bondInfo : list) {
                Map<String, String> uriMap = new HashMap<>();
                if (bondInfo.getId().equals(BondConstants.NHG_CODE)) {
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

    /**
     * 购买股票
     *
     * @param bondBuyLog
     */
    public void buyBond(BondBuyLog bondBuyLog) {
        BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(bondBuyLog.getGpId());
        bondBuyLog.setCreateTime(new Date());
        bondBuyLog.setOperTime(new Date());
        bondBuyLog.setSellCount(0);
        bondBuyLog.setSellIncome(0.00);
        bondBuyLog.setStatus(bondBuyLog.getStatus());

        if (bondBuyLog.getStatus() != null && bondBuyLog.getStatus() != 3) {
            bondBuyLog.setBuyDate(bondBuyLog.getBuyDate());
            double cost = BondUtils.getTaxation(bondInfo, bondBuyLog.getPrice() * bondBuyLog.getCount(), false);
            bondBuyLog.setCost(Double.parseDouble(String.format("%.2f", cost)));
            bondBuyLog.setTotalPrice(Double.parseDouble(String.format("%.2f", bondBuyLog.getPrice() * bondBuyLog.getCount())));
            bondBuyLog.setBuyCost(Double.parseDouble(String.format("%.2f", bondBuyLog.getCost())));
            refeshBondStatistics(bondBuyLog.getTotalPrice(), bondBuyLog.getCost());
        }
        bondBuyLogMapper.insertSelective(bondBuyLog);
    }


    /**
     * 更新股票统计数据
     *
     * @param value
     * @param cost
     */
    public void refeshBondStatistics(Double value, Double cost) {
        BondStatistics bondStatistics = bondStatisticsMapper.selectByPrimaryKey(1L);
        bondStatistics.setStock(Double.parseDouble(String.format("%.2f", bondStatistics.getStock() + value)));
        bondStatistics.setReady(Double.parseDouble(String.format("%.2f", bondStatistics.getReady() - value - cost)));
        bondStatisticsMapper.updateByPrimaryKey(bondStatistics);
    }

    /**
     * 出售股票
     *
     * @param bondSellLog
     */
    public void sellBond(BondSellLog bondSellLog) {
        BondBuyLog bondBuyLog = bondBuyLogMapper.selectByPrimaryKey(bondSellLog.getBuyId());

        BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(bondBuyLog.getGpId());

        //卖出税费计算
        double sellTaxation = BondUtils.getTaxation(bondInfo, bondSellLog.getPrice() * bondSellLog.getCount(), true);
        bondSellLog.setCost(sellTaxation);
        bondBuyLog.setCost(Double.parseDouble(String.format("%.2f", bondBuyLog.getCost() + bondSellLog.getCost())));

        //买入税费计算
        double buyTaxation = BondUtils.getTaxation(bondInfo, bondBuyLog.getPrice() * bondSellLog.getCount(), false);
        //计算收益 出售总价 - 买入总价 - 买卖费用
        double income = bondSellLog.getPrice() * bondSellLog.getCount() - bondBuyLog.getPrice() * bondSellLog.getCount() - bondSellLog.getCost() - buyTaxation;

        bondSellLog.setIncome(Double.parseDouble(String.format("%.2f", income)));
        bondSellLog.setGpId(bondInfo.getId());
        bondSellLog.setTotalCost(Double.parseDouble(String.format("%.2f", buyTaxation + sellTaxation)));
        bondSellLog.setTotalPrice(Double.parseDouble(String.format("%.2f", bondSellLog.getPrice() * bondSellLog.getCount())));
        bondSellLog.setCreateTime(bondSellLog.getCreateTime());

        bondBuyLog.setSellIncome(Double.parseDouble(String.format("%.2f", bondBuyLog.getSellIncome() + bondSellLog.getIncome())));
        bondBuyLog.setSellCount(bondBuyLog.getSellCount() + bondSellLog.getCount());
        bondBuyLog.setOperTime(new Date());
        //查看是否股票是否全部售出
        if (bondBuyLog.getCount() == bondBuyLog.getSellCount()) {
            bondBuyLog.setStatus((byte) 1);
        }
        int surplusCount = getBondNumber(bondInfo, bondBuyLog.getType()).intValue();
        bondSellLog.setSurplusCount(surplusCount);
        bondSellLogMapper.insert(bondSellLog);
        bondBuyLogMapper.updateByPrimaryKeySelective(bondBuyLog);

        refeshBondStatistics(-bondSellLog.getTotalPrice(), bondSellLog.getCost());

        //长线股票出售完后 添加到待购买池中
        if (bondBuyLog.getType() == BondConstants.LONG_LINE) {
            BondBuyLog buyLog = new BondBuyLog();
            buyLog.setPrice(bondBuyLog.getPrice());
            buyLog.setStatus(BondConstants.WAIT_BUY);
            buyLog.setCount(bondSellLog.getCount());
            buyLog.setType(BondConstants.LONG_LINE);
            buyLog.setGpId(bondSellLog.getGpId());
            buyBond(buyLog);
        }
    }

    public void initTask() {
        initBondStatisticsTask();
    }

    public void initBondStatisticsTask() {
        TotalProfitDTO totalProfitDTO = loadTotalProfitDTO();
        BondStatistics.BondStatisticsBuilder bondStatistics = BondStatistics.builder()
                .id(1L)
                .profitNumber(totalProfitDTO.getTotalProfitNumber())
                .lossNumber(totalProfitDTO.getTotalLossNumber().intValue())
                .stock(totalProfitDTO.getStockValue())
                .winning(totalProfitDTO.getAvgWinning())
                .profit(totalProfitDTO.getTotalProfit())
                .lossMoney(totalProfitDTO.getLossMoney())
                .cost(totalProfitDTO.getCost())
                .updateTime(new Date());
        bondStatisticsMapper.updateByPrimaryKeySelective(bondStatistics.build());

    }

}

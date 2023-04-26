package com.profit.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.profit.base.ResultDO;
import com.profit.base.domain.*;
import com.profit.base.mapper.*;
import com.profit.commons.constants.BondConstants;
import com.profit.commons.constants.ResultCode;
import com.profit.commons.utils.*;
import com.profit.comparator.ComparatorBondMarket;
import com.profit.comparator.ComparatorIncome;
import com.profit.dto.*;
import com.profit.service.BondService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;


@RestController
@RequestMapping("/bond")
public class BondController {
    @Resource
    private BondInfoMapper bondInfoMapper;
    @Resource
    private BondBuyLogMapper bondBuyLogMapper;
    @Resource
    private BondSellLogMapper bondSellLogMapper;
    @Resource
    private BondService bondService;
    @Resource
    private BondStatisticsMapper bondStatisticsMapper;
    @Resource
    private BaseDataMapper baseDataMapper;

    @GetMapping("list")
    public ResultDO<PageUtils<BondInfoDTO>> getBonds(@RequestParam Map<String, Object> params) throws Exception {
        BondInfoExample bondInfoExample = new BondInfoExample();
        BondInfoExample.Criteria criteria = bondInfoExample.createCriteria();
        if (params.get("isEtf") != null) {
            criteria.andIsEtfEqualTo(Byte.valueOf(params.get("isEtf").toString()));
        }
        criteria.andStatusEqualTo((byte) 0);
        bondInfoExample.setOrderByClause(" id " + params.get("order"));
        Page<Object> page = PageHelper.offsetPage(Integer.valueOf(params.get("offset").toString()), Integer.valueOf(params.get("limit").toString()), true);
        List<BondInfo> result = bondInfoMapper.selectByExample(bondInfoExample);
        if (result == null) {
            return new ResultDO<>(false, ResultCode.DB_ERROR, ResultCode.MSG_DB_ERROR, null);
        }
        List<BondInfoDTO> list = new ArrayList<>(result.size());

        //上月出售总收益
        Map<String, String> lastMonthMap = DateUtils.getLastMonthTime();
        Map<Object, Object> lastMonthProfitMap = bondService.getProfitByDate(lastMonthMap.get("startDate"), lastMonthMap.get("endDate"));

        //本月出售总收益
        Map<Object, Object> curMonthProfitMap = bondService.getProfitByDate(DateUtils.getMonthStart(), DateUtils.getMonthEnd());

        //今日出售总收益
//        Map<Object, Object> todayProfitMap = bondService.getProfitByDate(DateUtils.getTimeString(DateUtils.getTime(new Date(), 0, 0, 0)), DateUtils.getTimeString(DateUtils.getTime(new Date(), 23, 59, 59)));
        for (BondInfo bondInfo : result) {
            BondInfoDTO bondInfoDTO = bondService.loadBondInfoDTO(bondInfo);
            bondInfoDTO.setLastMonthProfit(lastMonthProfitMap.get(bondInfo.getId()) == null ? 0 : Double.parseDouble(String.format("%.2f", lastMonthProfitMap.get(bondInfo.getId()))));
            bondInfoDTO.setCurMonthProfit(curMonthProfitMap.get(bondInfo.getId()) == null ? 0 : Double.parseDouble(String.format("%.2f", curMonthProfitMap.get(bondInfo.getId()))));
//            bondInfoDTO.setTodayTProfit(todayProfitMap.get(bondInfo.getId()) == null ? 0 : Double.parseDouble(String.format("%.2f", todayProfitMap.get(bondInfo.getId()))));
            list.add(bondInfoDTO);
        }

        Collections.sort(list,new ComparatorBondMarket());
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, new PageUtils<>(page.getTotal(), list));
    }


    @PostMapping("insert")
    public ResultDO<Void> insert(@RequestBody BondInfo bondInfo) {
        bondInfo.setCreateTime(new Date());
        bondInfo.setUpdateTime(new Date());
        bondInfo.setStatus((byte) 0);
        bondInfo.setOldPrice(bondInfo.getPrice());
        bondInfoMapper.insert(bondInfo);
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

    @PostMapping("info")
    public ResultDO<TodayTaxationDTO> info(@RequestBody BondInfo bondInfo) {
        bondInfo = bondInfoMapper.selectByPrimaryKey(bondInfo.getId());
        TodayTaxationDTO todayTaxationDTO = bondService.loadToadyTaxationDTO(bondInfo.getId());
        todayTaxationDTO.setPrice(bondInfo.getPrice());
        todayTaxationDTO.setName(bondInfo.getName());
        bondService.getBondNumber(bondInfo, BondConstants.LONG_LINE);
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, todayTaxationDTO);
    }

    @PostMapping("update")
    public ResultDO<Void> update(@RequestBody BondInfo bondInfo) {
        bondInfo.setUpdateTime(new Date());
        bondInfoMapper.updateByPrimaryKeySelective(bondInfo);
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

    @PostMapping("analyse")
    public ResultDO<ProfitDTO> analyse(@RequestBody BondSellRequest bondSellRequest) {
        ProfitDTO profitDTO = bondService.totalProfit();
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, profitDTO);
    }

    @PostMapping("today/analyse")
    public ResultDO<TodayTaxationDTO> todayAnalyse(@RequestBody BondSellRequest bondSellRequest) {
        TotalProfitDTO totalProfitDTO = bondService.loadTotalProfitDTO();
        TodayTaxationDTO todayTaxationDTO = bondService.loadToadyTaxationDTO(null);
        todayTaxationDTO.setTodayStockProfit(totalProfitDTO.getTodayStockProfit());
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, todayTaxationDTO);
    }

    @PostMapping("total/analyse")
    public ResultDO<BondStatistics> totalAnalyse(@RequestBody BondSellRequest bondSellRequest) {
        BondStatistics bondStatistics = bondStatisticsMapper.selectByPrimaryKey(1L);
        double p = (bondStatistics.getStock() / (bondStatistics.getStock() + bondStatistics.getReady())) * 100;
        bondStatistics.setPosition(Double.parseDouble(String.format("%.2f", p)));
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, bondStatistics);
    }

    @PostMapping("sell/log")
    public ResultDO<List<BondSellDTO>> getSellLogs(@RequestBody BondSellRequest bondSellRequest) throws Exception {
        BondSellLogExample bondSellLogExample = new BondSellLogExample();
        bondSellLogExample.createCriteria().andBuyIdEqualTo(bondSellRequest.getBuyId());
        List<BondSellLog> list = bondSellLogMapper.selectByExample(bondSellLogExample);
        List<BondSellDTO> bondSellDTOS = new ArrayList<>();
        for (BondSellLog bondSellLog : list) {
            BondBuyLog bondBuyLog = bondBuyLogMapper.selectByPrimaryKey(bondSellLog.getBuyId());
            BondSellDTO bondSellDTO = BeanUtils.copyBean(new BondSellDTO(), bondSellLog);
            bondSellDTO.setProfitAndLoss(String.format("%.2f", ((bondSellDTO.getPrice() - bondBuyLog.getPrice()) / bondSellDTO.getPrice()) * 100) + "%");
            bondSellDTOS.add(bondSellDTO);
        }
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, bondSellDTOS);
    }

    @GetMapping("all")
    public ResultDO<PageUtils<BondBuyLogDTO>> allBondInfos(@RequestParam Map<String, Object> params) throws Exception {
        List<BondBuyLogDTO> list = new ArrayList<>();
        BondBuyLogExample bondBuyLogExample = new BondBuyLogExample();
        BondBuyLogExample.Criteria criteria = bondBuyLogExample.createCriteria();
        byte status = -1;
        if (params.get("status") != null) {
            status = Byte.valueOf(params.get("status").toString());
            criteria.andStatusEqualTo(status);
        }

        if (params.get("type") != null) {
            criteria.andTypeEqualTo(Byte.valueOf(params.get("type").toString()));
        }
        bondBuyLogExample.setOrderByClause(params.get("sort").toString() + " " + params.get("order").toString());
        Page<Object> page = PageHelper.offsetPage(Integer.valueOf(params.get("offset").toString()), Integer.valueOf(params.get("limit").toString()), true);
        List<BondBuyLog> result = bondBuyLogMapper.selectByExample(bondBuyLogExample);
        if (result == null) {
            return new ResultDO<>(false, ResultCode.DB_ERROR, ResultCode.MSG_DB_ERROR, null);
        }

        for (int i = 0; i < result.size(); i++) {
            BondBuyLog bondBuyLog = result.get(i);
            BondBuyLogDTO buyLogDTO = BeanUtils.copyBean(new BondBuyLogDTO(), bondBuyLog);
            BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(buyLogDTO.getGpId());
            if (bondInfo != null) {
                BondSellLogExample bondSellLogExample = new BondSellLogExample();
                bondSellLogExample.createCriteria().andBuyIdEqualTo(bondBuyLog.getId());
                List<BondSellLog> bondSellLogs = bondSellLogMapper.selectByExample(bondSellLogExample);
                for (BondSellLog bondSellLog : bondSellLogs) {
                    if (bondSellLog.getTotalCost() == null) {
                        Double buyCost = BondUtils.getTaxation(bondInfo, bondBuyLog.getPrice() * bondSellLog.getCount(), false);
                        bondSellLog.setTotalCost(Double.parseDouble(String.format("%.2f", bondSellLog.getCost() + buyCost)));
                        bondSellLogMapper.updateByPrimaryKey(bondSellLog);
                    }
                }

                buyLogDTO.setName(bondInfo.getName());
                buyLogDTO.setCurPrice(bondInfo.getPrice());
                //与上一个买点的格差
                String girdSpacing = "0";
                if (i > 0) {
                    girdSpacing = String.format("%.2f", ((bondBuyLog.getPrice() - result.get(i - 1).getPrice()) / bondBuyLog.getPrice()) * 100) + "%";
                }
                buyLogDTO.setGirdSpacing(girdSpacing);
                bondService.loadSellAvgPrice(bondBuyLog, buyLogDTO);

                //当前持股盈亏
                bondService.loadCurBondIncome(bondInfo, bondBuyLog, buyLogDTO);
                list.add(buyLogDTO);
            } else {
                LogUtil.error("allBondInfos error gpId:" + buyLogDTO.getGpId());
            }

        }
        if (status == BondConstants.NOT_SELL) {
            ComparatorIncome comparator = new ComparatorIncome();
            Collections.sort(list, comparator);
        }

        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, new PageUtils<>(page.getTotal(), list));
    }


}

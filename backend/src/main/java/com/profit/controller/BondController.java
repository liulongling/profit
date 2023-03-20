package com.profit.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.profit.base.ResultDO;
import com.profit.base.domain.*;
import com.profit.base.mapper.BondBuyLogMapper;
import com.profit.base.mapper.BondInfoMapper;
import com.profit.base.mapper.BondSellLogMapper;
import com.profit.commons.constants.ResultCode;
import com.profit.commons.utils.BeanUtils;
import com.profit.commons.utils.DateUtils;
import com.profit.commons.utils.PageUtils;
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

    @PostMapping("sell/log")
    public ResultDO<List<BondSellDTO>> getSellLogs(@RequestBody BondSellRequest bondSellRequest) throws Exception {
        BondSellLogExample bondSellLogExample = new BondSellLogExample();
        bondSellLogExample.createCriteria().andBuyIdEqualTo(bondSellRequest.getBuyId());
        List<BondSellLog> list = bondSellLogMapper.selectByExample(bondSellLogExample);
        List<BondSellDTO> bondSellDTOS = new ArrayList<>();
        for (BondSellLog bondSellLog : list) {
            BondBuyLog bondBuyLog = bondBuyLogMapper.selectByPrimaryKey(bondSellLog.getBuyId());
            BondSellDTO bondSellDTO = BeanUtils.copyBean(new BondSellDTO(), bondSellLog);
            bondSellDTO.setTotalPrice((long) (bondSellDTO.getPrice() * bondSellDTO.getCount()));
            bondSellDTO.setProfitAndLoss(String.format("%.2f", ((bondSellDTO.getPrice() - bondBuyLog.getPrice()) / bondSellDTO.getPrice()) * 100) + "%");
            bondSellDTOS.add(bondSellDTO);
        }
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, bondSellDTOS);
    }

    @GetMapping("all")
    public ResultDO<PageUtils<BondBuyLogDTO>> allBondInfos(@RequestParam Map<String, Object> params) throws Exception {
        List<BondInfo> bondInfos = bondInfoMapper.selectByExample(new BondInfoExample());
        List<BondBuyLogDTO> list = new ArrayList<>();
        for (BondInfo bondInfo : bondInfos) {
            String id = bondInfo.getId();
            BondBuyLogExample bondBuyLogExample = new BondBuyLogExample();
            BondBuyLogExample.Criteria criteria = bondBuyLogExample.createCriteria();
            criteria.andGpIdEqualTo(id);
            criteria.andStatusEqualTo((byte) 0);
            if (params.get("type") != null) {
                criteria.andTypeEqualTo(Byte.valueOf(params.get("type").toString()));
            }

            bondBuyLogExample.setOrderByClause("price desc");

            List<BondBuyLog> result = bondBuyLogMapper.selectByExample(bondBuyLogExample);
            if (result == null) {
                return new ResultDO<>(false, ResultCode.DB_ERROR, ResultCode.MSG_DB_ERROR, null);
            }

            for (int i = 0; i < result.size(); i++) {
                BondBuyLog bondBuyLog = result.get(i);
                BondBuyLogDTO buyLogDTO = BeanUtils.copyBean(new BondBuyLogDTO(), bondBuyLog);
                buyLogDTO.setName(bondInfo.getName());

                //与上一个买点的格差
                String girdSpacing = "0";
                if (i > 0) {
                    girdSpacing = String.format("%.2f", ((bondBuyLog.getPrice() - result.get(i - 1).getPrice()) / bondBuyLog.getPrice()) * 100) + "%";
                }
                buyLogDTO.setGirdSpacing(girdSpacing);
                bondService.loadSellAvgPrice(bondBuyLog, buyLogDTO);

                //当前持股盈亏
                if (bondBuyLog.getCount() > bondBuyLog.getSellCount()) {
                    bondService.loadCurBondIncome(bondInfo, bondBuyLog, buyLogDTO);
                    if (buyLogDTO.getIncome() > 0) {
                        list.add(buyLogDTO);
                        ComparatorIncome comparator = new ComparatorIncome();
                        Collections.sort(list, comparator);
                    }
                }
            }
        }
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, new PageUtils<>(list.size(), list));
    }

    @GetMapping("list")
    public ResultDO<PageUtils<BondInfoDTO>> getBonds(@RequestParam Map<String, Object> params) throws Exception {
        BondInfoExample bondInfoExample = new BondInfoExample();
        if (params.get("isEtf") != null) {
            bondInfoExample.createCriteria().andIsEtfEqualTo(Byte.valueOf(params.get("isEtf").toString()));
        }
        bondInfoExample.setOrderByClause(" id " + params.get("order"));
        Page<Object> page = PageHelper.startPage(Integer.valueOf(params.get("offset").toString()), Integer.valueOf(params.get("limit").toString()), true);
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
        Map<Object, Object> todayProfitMap = bondService.getProfitByDate(DateUtils.getTimeString(DateUtils.getTime(new Date(), 0, 0, 0)), DateUtils.getTimeString(DateUtils.getTime(new Date(), 23, 59, 59)));


        for (BondInfo bondInfo : result) {
            BondInfoDTO bondInfoDTO = BeanUtils.copyBean(new BondInfoDTO(), bondInfo);
            Double stubProfit = bondBuyLogMapper.sumSellIncome(bondInfo.getId(), (byte) 1);
            if (stubProfit == null) stubProfit = 0.00;
            bondInfoDTO.setStubProfit(Double.parseDouble(String.format("%.2f", stubProfit)));
            Double gridProfit = bondBuyLogMapper.sumSellIncome(bondInfo.getId(), (byte) 0);
            if (gridProfit == null) gridProfit = 0.00;
            bondInfoDTO.setGridProfit(Double.parseDouble(String.format("%.2f", gridProfit)));

            bondInfoDTO.setStubCount(bondService.getBondNumber(bondInfo, (byte) 1));
            bondInfoDTO.setGridCount(bondService.getBondNumber(bondInfo, (byte) 0));

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

            bondInfoDTO.setLastMonthProfit(lastMonthProfitMap.get(bondInfo.getId()) == null ? 0 : Double.parseDouble(String.format("%.2f", lastMonthProfitMap.get(bondInfo.getId()))));
            bondInfoDTO.setCurMonthProfit(curMonthProfitMap.get(bondInfo.getId()) == null ? 0 : Double.parseDouble(String.format("%.2f", curMonthProfitMap.get(bondInfo.getId()))));
            bondInfoDTO.setTodayTProfit(todayProfitMap.get(bondInfo.getId()) == null ? 0 : Double.parseDouble(String.format("%.2f", todayProfitMap.get(bondInfo.getId()))));

            list.add(bondInfoDTO);
        }

        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, new PageUtils<>(page.getTotal(), list));
    }


    @PostMapping("insert")
    public ResultDO<Void> insert(@RequestBody BondInfo bondInfo) {
        bondInfo.setCreateTime(new Date());
        bondInfo.setUpdateTime(new Date());
        bondInfoMapper.insert(bondInfo);
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

    @PostMapping("info")
    public ResultDO<BondInfo> info(@RequestBody BondInfo bondInfo) {
        bondInfo = bondInfoMapper.selectByPrimaryKey(bondInfo.getId());
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, bondInfo);
    }

    @PostMapping("update")
    public ResultDO<Void> update(@RequestBody BondInfo bondInfo) {
        bondInfo.setUpdateTime(new Date());
        bondInfoMapper.updateByPrimaryKeySelective(bondInfo);
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

    @PostMapping("analyse")
    public ResultDO<List<ProfitDTO>> analyse(@RequestBody BondSellRequest bondSellRequest) {
        bondSellRequest = new BondSellRequest();
        bondSellRequest.setType(0);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        bondSellRequest.setStartTime(DateUtils.getTimeString(DateUtils.getBeginTime(year, 1)));
        bondSellRequest.setEndTime(DateUtils.getTimeString(DateUtils.getBeginTime(year, 12)));

        Map<String, ProfitDTO> map = new HashMap<>();

        bondService.loadProfitDTOData(map, bondSellRequest);
        bondSellRequest.setType(1);
        bondService.loadProfitDTOData(map, bondSellRequest);

        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, new ArrayList<>(map.values()));
    }

}

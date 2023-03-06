package com.profit.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.profit.base.ResultDO;
import com.profit.base.domain.BondBuyLog;
import com.profit.base.domain.BondBuyLogExample;
import com.profit.base.domain.BondInfo;
import com.profit.base.domain.BondInfoExample;
import com.profit.base.mapper.BondBuyLogMapper;
import com.profit.base.mapper.BondInfoMapper;
import com.profit.base.mapper.BondSellLogMapper;
import com.profit.commons.constants.ResultCode;
import com.profit.commons.utils.BeanUtils;
import com.profit.commons.utils.DateUtils;
import com.profit.commons.utils.PageUtils;
import com.profit.dto.BondInfoDTO;
import com.profit.dto.BondSellRequest;
import com.profit.dto.ChartData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/bond")
public class BondController {
    @Resource
    private BondInfoMapper bondInfoMapper;
    @Resource
    private BondBuyLogMapper bondBuyLogMapper;
    @Resource
    private BondSellLogMapper bondSellLogMapper;

    @GetMapping("list")
    public ResultDO<PageUtils<BondInfoDTO>> getBonds(@RequestParam Map<String, Object> params) {
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


        BondSellRequest bondSellRequest = new BondSellRequest();
        //本月出售总收益
        bondSellRequest.setStartTime(DateUtils.getMonthStart());
        bondSellRequest.setEndTime(DateUtils.getMonthEnd());
        List<Map<Object, Object>> curMonthProfitList = bondSellLogMapper.listGroupByGpId(bondSellRequest);
        Map<Object, Object> curMonthProfitMap = BeanUtils.list2Map(curMonthProfitList, "gpId", "income");

        //今日出售总收益
        bondSellRequest.setStartTime(DateUtils.getTime(new Date(), 0, 0, 0));
        bondSellRequest.setEndTime(DateUtils.getTime(new Date(), 23, 59, 59));
        List<Map<Object, Object>> todayProfitList = bondSellLogMapper.listGroupByGpId(bondSellRequest);
        Map<Object, Object> todayProfitMap = BeanUtils.list2Map(todayProfitList, "gpId", "income");


        for (BondInfo bondInfo : result) {
            BondInfoDTO bondInfoDTO = BeanUtils.copyBean(new BondInfoDTO(), bondInfo);
            Double stubProfit = bondBuyLogMapper.sumSellIncome(bondInfo.getId(), (byte) 1);
            if (stubProfit == null) stubProfit = 0.00;
            bondInfoDTO.setStubProfit(Double.parseDouble(String.format("%.2f", stubProfit)));
            Double gridProfit = bondBuyLogMapper.sumSellIncome(bondInfo.getId(), (byte) 0);
            if (gridProfit == null) gridProfit = 0.00;
            bondInfoDTO.setGridProfit(Double.parseDouble(String.format("%.2f", gridProfit)));

            bondInfoDTO.setStubCount(count(bondInfo, (byte) 1));
            bondInfoDTO.setGridCount(count(bondInfo, (byte) 0));

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
                        total += bondInfo.getPrice() * surplusCount - bondBuyLog.getPrice() * surplusCount;
                    }
                }
                bondInfoDTO.setGpProfit(Double.parseDouble(String.format("%.2f", total)));
            }

            bondInfoDTO.setCurMonthProfit(curMonthProfitMap.get(bondInfo.getId()) == null ? 0 : Double.parseDouble(String.format("%.2f", curMonthProfitMap.get(bondInfo.getId()))));
            bondInfoDTO.setTodayTProfit(todayProfitMap.get(bondInfo.getId()) == null ? 0 : Double.parseDouble(String.format("%.2f", todayProfitMap.get(bondInfo.getId()))));

            list.add(bondInfoDTO);
        }

        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, new PageUtils<>(page.getTotal(), list));
    }


    public long count(BondInfo bondInfo, byte type) {
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
    public ResultDO<Void> analyse(@RequestParam String startDate,
                                  @RequestParam String endDate) {
        ChartData chartData = new ChartData();
        chartData.getLabels().addAll(DateUtils.getDateList(startDate, endDate));

        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

}

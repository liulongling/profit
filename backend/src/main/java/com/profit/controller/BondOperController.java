package com.profit.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.profit.base.ResultDO;
import com.profit.base.domain.*;
import com.profit.base.mapper.BondBuyLogMapper;
import com.profit.base.mapper.BondInfoMapper;
import com.profit.base.mapper.BondSellLogMapper;
import com.profit.commons.constants.BondConstants;
import com.profit.commons.constants.ResultCode;
import com.profit.commons.utils.*;
import com.profit.comparator.ComparatorBondSell;
import com.profit.dto.BondBuyLogDTO;
import com.profit.dto.BondBuyRequest;
import com.profit.service.BondService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;


@RestController
@RequestMapping("/bond/operating")
public class BondOperController {
    @Resource
    private BondBuyLogMapper bondBuyLogMapper;
    @Resource
    private BondInfoMapper bondInfoMapper;
    @Resource
    private BondSellLogMapper bondSellLogMapper;
    @Resource
    private BondService bondService;

    @GetMapping("list")
    public ResultDO<PageUtils<BondBuyLogDTO>> getBonds(@RequestParam Map<String, Object> params) {
        String id = params.get("id").toString();
        if (id == null) {
            return new ResultDO<>(false, ResultCode.DB_ERROR, ResultCode.MSG_DB_ERROR, null);
        }
        BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(id);
        BondBuyLogExample bondBuyLogExample = new BondBuyLogExample();
        BondBuyLogExample.Criteria criteria = bondBuyLogExample.createCriteria();
        criteria.andGpIdEqualTo(id);
        bondBuyLogExample.setOrderByClause("oper_time desc");
        if (params.get("type") != null) {
            criteria.andTypeEqualTo(Byte.valueOf(params.get("type").toString()));
            if (params.get("type").equals("0")) {
                bondBuyLogExample.setOrderByClause("price desc");
            }
        }

        byte status = 0;
        if (params.get("status") != null) {
            status = Byte.valueOf(params.get("status").toString());
            criteria.andStatusEqualTo(status);
            if (status == 1) {
                bondBuyLogExample.setOrderByClause("buy_date desc");
            } else {
                bondBuyLogExample.setOrderByClause("price desc");
            }
        }

        Page<Object> page = PageHelper.offsetPage(Integer.valueOf(params.get("offset").toString()), Integer.valueOf(params.get("limit").toString()));
        List<BondBuyLog> result = bondBuyLogMapper.selectByExample(bondBuyLogExample);
        if (result == null) {
            return new ResultDO<>(false, ResultCode.DB_ERROR, ResultCode.MSG_DB_ERROR, null);
        }
        List<BondBuyLogDTO> list = new ArrayList<>(result.size());
        for (int i = 0; i < result.size(); i++) {
            BondBuyLog bondBuyLog = result.get(i);
            //查看是否股票是否全部售出
            if (bondBuyLog.getCount().intValue() == bondBuyLog.getSellCount().intValue()) {
                if (bondBuyLog.getStatus() == 0) {
                    bondBuyLog.setStatus((byte) 1);
                    bondBuyLogMapper.updateByPrimaryKeySelective(bondBuyLog);
                }
            }
            BondBuyLogDTO buyLogDTO = BeanUtils.copyBean(new BondBuyLogDTO(), bondBuyLog);
            buyLogDTO.setName(bondInfo.getName());
            //卖出均价
            bondService.loadSellAvgPrice(bondBuyLog, buyLogDTO);
            //当前持股盈亏
            bondService.loadCurBondIncome(bondInfo, bondBuyLog, buyLogDTO);
            //与上一个买点的格差
            String girdSpacing = "0";
            if (i > 0) {
                girdSpacing = String.format("%.2f", ((bondBuyLog.getPrice() - result.get(i - 1).getPrice()) / bondBuyLog.getPrice()) * 100) + "%";
            }
            buyLogDTO.setGirdSpacing(girdSpacing);
            list.add(buyLogDTO);
            if (status == 1) {
                ComparatorBondSell comparator = new ComparatorBondSell();
                Collections.sort(list, comparator);
            }
        }

        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, new PageUtils<>(page.getTotal(), list));
    }

    @PostMapping("buy")
    public ResultDO<Void> buy(@RequestBody BondBuyRequest bondBuyRequest) {
        BondBuyLog bondBuyLog = BeanUtils.copyBean(new BondBuyLog(), bondBuyRequest);
        bondService.buyBond(bondBuyLog);
        if (bondBuyRequest.getSellPrice() != null && bondBuyRequest.getSellPrice() > 0 && bondBuyLog.getId() > 0) {
            BondSellLog bondSellLog = new BondSellLog();
            bondSellLog.setBuyId(bondBuyLog.getId());
            bondSellLog.setGpId(bondBuyLog.getGpId());
            bondSellLog.setPrice(bondBuyRequest.getSellPrice());
            bondSellLog.setCount(bondBuyRequest.getCount());
            Date date = DateUtils.string2Date(bondBuyRequest.getBuyDate(), DateUtils.DATE_PATTERM);
            date.setHours(8);
            bondSellLog.setCreateTime(date);
            bondService.sellBond(bondSellLog);
        }

        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

    @PostMapping("sell")
    public ResultDO<Void> sell(@RequestBody BondSellLog bondSellLog) {
        bondService.sellBond(bondSellLog);
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }


    @PostMapping("update")
    public ResultDO<Void> update(@RequestBody BondBuyLog bondBuyLogRequest) {
        BondBuyLog bondBuyLog = bondBuyLogMapper.selectByPrimaryKey(bondBuyLogRequest.getId());
        BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(bondBuyLog.getGpId());
        bondBuyLog.setOperTime(new Date());
        bondBuyLog.setPrice(bondBuyLogRequest.getPrice());
        bondBuyLog.setCount(bondBuyLogRequest.getCount());
        bondBuyLog.setBuyDate(bondBuyLogRequest.getBuyDate());

        boolean isBuy = false;
        if (bondBuyLog != null && bondBuyLog.getStatus() == BondConstants.WAIT_BUY && bondBuyLogRequest.getStatus() == BondConstants.NOT_SELL) {
            isBuy = true;
        }

        if (bondBuyLogRequest.getType() != null) {
            bondBuyLog.setType(bondBuyLogRequest.getType());
        }

        if (bondBuyLogRequest.getStatus() != null) {
            bondBuyLog.setStatus(bondBuyLogRequest.getStatus());
        }

        //未出售的状态下才能修改税费
        if (bondBuyLog.getStatus() == BondConstants.NOT_SELL) {
            Double buyCost = BondUtils.getTaxation(bondInfo, bondBuyLog.getPrice() * bondBuyLog.getCount(), false);
            bondBuyLog.setCost(Double.parseDouble(String.format("%.2f", buyCost)));
            bondBuyLog.setBuyCost(Double.parseDouble(String.format("%.2f", buyCost)));
            bondBuyLog.setTotalPrice(Double.parseDouble(String.format("%.2f", bondBuyLogRequest.getPrice() * bondBuyLogRequest.getCount())));
        }


        bondBuyLogMapper.updateByPrimaryKeySelective(bondBuyLog);

        if (isBuy) {
            bondService.refeshBondStatistics(bondBuyLog.getCount() * bondBuyLog.getPrice(), bondBuyLog.getCost());
        }
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

}

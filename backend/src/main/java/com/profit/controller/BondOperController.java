package com.profit.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.profit.base.ResultDO;
import com.profit.base.domain.*;
import com.profit.base.mapper.BondBuyLogMapper;
import com.profit.base.mapper.BondInfoMapper;
import com.profit.base.mapper.BondSellLogMapper;
import com.profit.commons.constants.ResultCode;
import com.profit.commons.utils.*;
import com.profit.dto.BondBuyLogDTO;
import com.profit.service.BondService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


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
        if (params.get("status") != null) {
            byte status = Byte.valueOf(params.get("status").toString());
            criteria.andStatusEqualTo(status);
            if (status == 1) {
                bondBuyLogExample.setOrderByClause("oper_time desc");
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
        }

        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, new PageUtils<>(page.getTotal(), list));
    }

    @PostMapping("insert")
    public ResultDO<Void> insert(@RequestBody BondBuyLog bondBuyLog) {
        bondBuyLog.setCreateTime(new Date());
        bondBuyLog.setOperTime(new Date());
        bondBuyLog.setSellCount(0);
        bondBuyLog.setSellIncome(0.00);
        bondBuyLog.setStatus((byte) 0);
        BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(bondBuyLog.getGpId());
        bondBuyLog.setCost(BondUtils.getTaxation(bondInfo.getIsEtf() == 1, bondInfo.getPlate(), bondBuyLog.getPrice() * bondBuyLog.getCount(), false));
        bondBuyLogMapper.insert(bondBuyLog);
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

    @PostMapping("sell")
    public ResultDO<Void> sell(@RequestBody BondSellLog bondSellLog) {
        BondBuyLog bondBuyLog = bondBuyLogMapper.selectByPrimaryKey(bondSellLog.getBuyId());

        BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(bondBuyLog.getGpId());

        //卖出税费计算
        double sellTaxation = BondUtils.getTaxation(bondInfo.getIsEtf() == 1, bondInfo.getPlate(), bondSellLog.getPrice() * bondSellLog.getCount(), true);
        LogUtil.info(bondSellLog.getId() + "sellTaxation" + sellTaxation);
        bondSellLog.setCost(sellTaxation);
        bondBuyLog.setCost(Double.parseDouble(String.format("%.3f", bondBuyLog.getCost() + bondSellLog.getCost())));

        //买入税费计算
        double buyTaxation = BondUtils.getTaxation(bondInfo.getIsEtf() == 1, bondInfo.getPlate(), bondBuyLog.getPrice() * bondSellLog.getCount(), false);
        //计算收益 出售总价 - 买入总价 - 买卖费用
        double income = bondSellLog.getPrice() * bondSellLog.getCount() - bondBuyLog.getPrice() * bondSellLog.getCount() - bondSellLog.getCost() - buyTaxation;

        bondSellLog.setIncome(Double.parseDouble(String.format("%.3f", income)));
        bondBuyLog.setSellIncome(Double.parseDouble(String.format("%.3f", bondBuyLog.getSellIncome() + bondSellLog.getIncome())));
        bondBuyLog.setSellCount(bondBuyLog.getSellCount() + bondSellLog.getCount());
        bondBuyLog.setOperTime(new Date());
        bondSellLog.setGpId(bondInfo.getId());
        //查看是否股票是否全部售出
        if (bondBuyLog.getCount() == bondBuyLog.getSellCount()) {
            bondBuyLog.setStatus((byte) 1);
        }
        int surplusCount = bondService.getBondNumber(bondInfo, bondBuyLog.getType()).intValue();
        bondSellLog.setSurplusCount(surplusCount);
        bondSellLogMapper.insert(bondSellLog);
        bondBuyLogMapper.updateByPrimaryKeySelective(bondBuyLog);

        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }


    @PostMapping("update")
    public ResultDO<Void> update(@RequestBody BondBuyLog bondBuyLog) {
        BondBuyLog bondBuyLog1 = bondBuyLogMapper.selectByPrimaryKey(bondBuyLog.getId());


        BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(bondBuyLog.getGpId());
        bondBuyLog.setOperTime(new Date());

        //未出售的状态下才能修改税费
        if (bondBuyLog1.getSellCount() == 0) {
            Double buyCost = BondUtils.getTaxation(bondInfo.getIsEtf() == 1, bondInfo.getPlate(), bondBuyLog.getPrice() * bondBuyLog.getCount(), false);
            bondBuyLog.setCost(buyCost);
        }

        bondBuyLogMapper.updateByPrimaryKeySelective(bondBuyLog);
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

}

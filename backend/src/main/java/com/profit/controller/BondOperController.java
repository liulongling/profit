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
import com.profit.comparator.ComparatorGpId;
import com.profit.dto.BondBuyLogDTO;
import com.profit.dto.BondBuyRequest;
import com.profit.dto.BondTransactionDTO;
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

    @GetMapping("transactionLogs")
    public ResultDO<PageUtils<BondTransactionDTO>> transactionLogs(@RequestParam Map<String, Object> params) {

        int type = Integer.parseInt(params.get("type").toString());

        List<BondTransactionDTO> list = new ArrayList<>();

        if (type == 0 || type == 1) {
            BondBuyLogExample bondBuyLogExample = new BondBuyLogExample();
            BondBuyLogExample.Criteria criteria = bondBuyLogExample.createCriteria();
            if (params.get("id") != null) {
                criteria.andGpIdEqualTo(params.get("id").toString());
            }
            Date startDate = DateUtils.getNeverDayStartTime(5);
            String endDate = DateUtils.getDateString(DateUtils.getTodayDateTime(23, 59, 59));
            criteria.andBuyDateBetween(DateUtils.getDateString(startDate), endDate);

            bondBuyLogExample.setOrderByClause("oper_time desc");
            List<BondBuyLog> buyLogs = bondBuyLogMapper.selectByExample(bondBuyLogExample);
            if (buyLogs != null) {
                for (BondBuyLog bondBuyLog : buyLogs) {
                    BondTransactionDTO bondTransactionDTO = BeanUtils.copyBean(new BondTransactionDTO(), bondBuyLog);
                    bondTransactionDTO.setOperateDate(bondBuyLog.getBuyDate());
                    bondTransactionDTO.setRemarks("买入");

                    BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(bondBuyLog.getGpId());
                    if (bondInfo != null) {
                        bondTransactionDTO.setName(bondInfo.getName());
                    } else {
                        LogUtil.error("id is null" + bondBuyLog.getGpId());
                    }

                    list.add(bondTransactionDTO);
                }
            }
        }

        if (type == 0 || type == 2) {
            BondSellLogExample bondSellLogExample = new BondSellLogExample();
            BondSellLogExample.Criteria bondSellLogExampleCriteria = bondSellLogExample.createCriteria();
            if (params.get("id") != null) {
                bondSellLogExampleCriteria.andGpIdEqualTo(params.get("id").toString());
            }
            Date startDate = DateUtils.getNeverDayStartTime(5);

            bondSellLogExampleCriteria.andCreateTimeBetween(startDate, DateUtils.getTodayDateTime(23, 59, 59));
            bondSellLogExample.setOrderByClause("create_time ,buy_id desc");
            List<BondSellLog> sellLogs = bondSellLogMapper.selectByExample(bondSellLogExample);
            if (sellLogs != null) {
                for (BondSellLog bondSellLog : sellLogs) {
                    BondTransactionDTO bondTransactionDTO = BeanUtils.copyBean(new BondTransactionDTO(), bondSellLog);
                    bondTransactionDTO.setOperateDate(DateUtils.getDateString(bondSellLog.getCreateTime()));
                    bondTransactionDTO.setRemarks("卖出");

                    BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(bondSellLog.getGpId());
                    bondTransactionDTO.setName(bondInfo.getName());
                    list.add(bondTransactionDTO);
                }
            }
        }
        ComparatorGpId comparator = new ComparatorGpId();
        Collections.sort(list, comparator);
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, new PageUtils<>(list.size(), list));
    }

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
//                bondBuyLogExample.setOrderByClause("buy_date desc");
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

//            repair(bondBuyLog);

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
            buyLogDTO.setSubCount(bondBuyLog.getCount() - bondBuyLog.getSellCount());
            if (bondBuyLog.getFinancing() == 1) {
                buyLogDTO.setName(buyLogDTO.getName() + "(融)");
            }
            buyLogDTO.setInterest(bondBuyLog.countInterest());

            list.add(buyLogDTO);
            if (status == 1) {
                Collections.sort(list, new ComparatorBondSell());
            }
        }

        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, new PageUtils<>(page.getTotal(), list));
    }

    public void repair(BondBuyLog bondBuyLog) {
        //纠正税费错误bug
        if (bondBuyLog.getInterest() > 0) {
            BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(bondBuyLog.getGpId());
            BondSellLogExample bondSellLogExample = new BondSellLogExample();
            bondSellLogExample.createCriteria().andBuyIdEqualTo(bondBuyLog.getId());
            List<BondSellLog> list = bondSellLogMapper.selectByExample(bondSellLogExample);
            for (BondSellLog bondSellLog : list) {
                if (bondSellLog.getPrice() == 0) {
                    continue;
                }
                //卖出税费计算
                double sellTaxation = BondUtils.getTaxation(bondInfo, bondSellLog.getPrice() * bondSellLog.getCount(), true);
                //买入税费计算
                double buyTaxation = BondUtils.getTaxation(bondInfo, bondBuyLog.getPrice() * bondSellLog.getCount(), false);
                //计算收益 出售总价 - 买入总价 - 买卖费用
                double income = bondSellLog.getPrice() * bondSellLog.getCount() - bondBuyLog.getPrice() * bondSellLog.getCount() - sellTaxation - buyTaxation;
                income = Double.parseDouble(String.format("%.2f", income));
                double startIncome = Double.parseDouble(String.format("%.2f", bondSellLog.getIncome()));
                if (startIncome != income) {
                    System.out.println("" + bondSellLog.getIncome() + " after:" + income);
                    bondSellLog.setIncome(income);
                    bondSellLogMapper.updateByPrimaryKeySelective(bondSellLog);
                }
            }


//            double income = 0;
//            for (BondSellLog b : list) {
//                if(b.getPrice() == 0){
//                    income += b.getIncome();
//                }
//            }

//            if (bondBuyLog.getInterest() > -income) {
//                BondSellLog bondSellLog = new BondSellLog();
//                bondSellLog.setBuyId(bondBuyLog.getId());
//                bondSellLog.setGpId(bondBuyLog.getGpId());
//                bondSellLog.setCost(0.0);
//                bondSellLog.setTotalCost(0.0);
//                bondSellLog.setTotalPrice(0.0);
//                bondSellLog.setPrice(0.0);
//                bondSellLog.setCount(0);
//                bondSellLog.setIncome(-bondBuyLog.getInterest());
//                bondSellLog.setCreateTime(new Date());
//                bondSellLogMapper.insert(bondSellLog);
//            }


//            double cost = 0;
//            for (BondSellLog bondSellLog : list) {
//                cost += bondSellLog.getCost();
//            }
//            bondBuyLog.setCost(Double.parseDouble(String.format("%.2f", cost)));
//            bondBuyLogMapper.updateByPrimaryKeySelective(bondBuyLog);
        }

    }

    @PostMapping("buy")
    public ResultDO<Void> buy(@RequestBody BondBuyRequest bondBuyRequest) {
        if (bondBuyRequest.getGpId().equals(BondConstants.NHG_CODE)) {
            bondService.buyGz(bondBuyRequest);
        } else {
            BondBuyLog bondBuyLog = BeanUtils.copyBean(new BondBuyLog(), bondBuyRequest);
            bondService.buyBond(bondBuyLog);
            if (bondBuyRequest.getSellPrice() != null && bondBuyRequest.getSellPrice() > 0 && bondBuyLog.getId() > 0) {
                Date date = DateUtils.string2Date(bondBuyRequest.getSellDate(), DateUtils.DATE_PATTERM);
                date.setHours(8);
                BondSellLog bondSellLog = new BondSellLog();
                bondSellLog.setBuyId(bondBuyLog.getId());
                bondSellLog.setGpId(bondBuyLog.getGpId());
                bondSellLog.setPrice(bondBuyRequest.getSellPrice());
                bondSellLog.setCount(bondBuyRequest.getCount());
                bondSellLog.setCreateTime(date);
                bondSellLog.setFreeze(0.5);
                bondService.sellBond(bondSellLog);
            }
        }
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

    @PostMapping("sell")
    public ResultDO<Void> sell(@RequestBody BondSellLog bondSellLog) {
        bondService.sellBond(bondSellLog);
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

    @PostMapping("back")
    public ResultDO<Void> back(@RequestBody BondBuyLogDTO bondBuyLogDTO) {
        return bondService.backBond(bondBuyLogDTO);
    }


    @PostMapping("update")
    public ResultDO<Void> update(@RequestBody BondBuyLog bondBuyLogRequest) {
        BondBuyLog bondBuyLog = bondBuyLogMapper.selectByPrimaryKey(bondBuyLogRequest.getId());
        BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(bondBuyLog.getGpId());
        bondBuyLog.setOperTime(new Date());
        bondBuyLog.setPrice(bondBuyLogRequest.getPrice());
        bondBuyLog.setCount(bondBuyLogRequest.getCount());
        bondBuyLog.setBuyDate(bondBuyLogRequest.getBuyDate());
        bondBuyLog.setFinancing(bondBuyLogRequest.getFinancing());
        bondBuyLog.setRemarks(bondBuyLogRequest.getRemarks());

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
            bondService.refeshBondStatistics(bondBuyLog.getCount() * bondBuyLog.getPrice(), bondBuyLog.getCost(), 0.5);
        }
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

    @PostMapping("delete")
    public ResultDO<Void> delete(@RequestBody BondBuyLog bondBuyLog) {
        bondBuyLogMapper.deleteByPrimaryKey(bondBuyLog.getId());
        //查询是否有出售
        BondSellLogExample bondSellLogExample = new BondSellLogExample();
        bondSellLogExample.createCriteria().andBuyIdEqualTo(bondBuyLog.getId());
        List<BondSellLog> bondSellLogs = bondSellLogMapper.selectByExample(bondSellLogExample);
        if (bondSellLogs != null) {
            for (BondSellLog bondSellLog : bondSellLogs) {
                bondSellLogMapper.deleteByPrimaryKey(bondSellLog.getId());
            }
        }

        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

}

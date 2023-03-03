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
import com.profit.commons.constants.ResultCode;
import com.profit.commons.utils.BeanUtils;
import com.profit.commons.utils.PageUtils;
import com.profit.dto.BondInfoDTO;
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

    @PostMapping("update")
    public ResultDO<Void> update(@RequestBody BondInfo bondInfo) {
        bondInfo.setUpdateTime(new Date());
        bondInfoMapper.updateByPrimaryKeySelective(bondInfo);
        return new ResultDO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, null);
    }

}

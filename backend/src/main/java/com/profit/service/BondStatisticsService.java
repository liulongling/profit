package com.profit.service;

import com.profit.base.ResultDO;
import com.profit.base.domain.*;
import com.profit.base.mapper.*;
import com.profit.commons.constants.JobEnum;
import com.profit.commons.utils.BondUtils;
import com.profit.commons.utils.DateUtils;
import com.profit.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class BondStatisticsService {
    @Resource
    private BondService bondService;
    @Resource
    private BondInfoMapper bondInfoMapper;
    @Resource
    private BondSellLogMapper bondSellLogMapper;
    @Resource
    private BondBuyLogMapper bondBuyLogMapper;
    @Resource
    private BondStatisticsLogMapper bondStatisticsLogMapper;
    @Resource
    private TimeTaskJobMapper timeTaskJobMapper;

    public volatile boolean executeing = false;

    public TimeTaskJob initTimeTaskInfo() {
        TimeTaskJob timeTaskJob = new TimeTaskJob();
        timeTaskJob.setSyncTime(new Date());
        timeTaskJob.setJobName(JobEnum.SYS_STATISTICS_JOB.getName());
        timeTaskJob.setCreateTime(new Date());
        timeTaskJob.setUpdateTime(new Date());
        timeTaskJob.setSyncFlag(0);
        timeTaskJobMapper.insert(timeTaskJob);
        return timeTaskJob;
    }

    /**
     * 同步交通数据
     */
    public void sysData() {
        if (executeing) {
            return;
        }
        executeing = !executeing;

        TimeTaskJob timeTaskJob = timeTaskJobMapper.selectByPrimaryKey(JobEnum.SYS_STATISTICS_JOB.getCode());
        if (timeTaskJob == null) {
            timeTaskJob = initTimeTaskInfo();
        }

        if (DateUtils.isToday(timeTaskJob.getSyncTime())) {
            return;
        }

        TotalProfitDTO totalProfitDTO = bondService.loadTotalProfitDTO();

        BondStatisticsLog bondStatisticsLog = new BondStatisticsLog();
        bondStatisticsLog.setStock(totalProfitDTO.getStockValue());
        bondStatisticsLog.setReady(totalProfitDTO.getReady());
        bondStatisticsLog.setProfit(bondService.gpProfit());

        //查询负债
        BondBuyLogExample bondBuyLogExample = new BondBuyLogExample();
        bondBuyLogExample.createCriteria().andFinancingEqualTo((byte) 1);
        List<BondBuyLog> buyLogs = bondBuyLogMapper.selectByExample(bondBuyLogExample);
        Double liability = 0.0;
        for (BondBuyLog bondBuyLog : buyLogs) {
            BondInfo bondInfo = bondInfoMapper.selectByPrimaryKey(bondBuyLog.getGpId());
            Double stock = (bondBuyLog.getCount() - bondBuyLog.getSellCount()) * bondBuyLog.getPrice() - bondBuyLog.getBackMoney();
            liability += stock + BondUtils.getTaxation(bondInfo, stock, false);
        }
        bondStatisticsLog.setLiability(liability);
        bondStatisticsLog.setCreateTime(new Date());
        bondStatisticsLogMapper.insertSelective(bondStatisticsLog);

        timeTaskJob.setSyncTime(new Date());
        timeTaskJob.setSyncFlag(1);
        timeTaskJobMapper.updateByPrimaryKeySelective(timeTaskJob);

        executeing = !executeing;
    }
}

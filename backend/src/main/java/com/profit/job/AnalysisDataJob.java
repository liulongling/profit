package com.profit.job;

import com.profit.commons.utils.LogUtil;
import com.profit.service.BondService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author:liulongling
 * @Date:2022/4/2 15:30
 */
@Component
public class AnalysisDataJob {
    @Resource
    private BondService bondService;

    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void task() {
        LogUtil.info("定时任务[task]开始执行");
        bondService.refurbishBondPrice();
        LogUtil.info("定时任务[task]执行结束");

    }

    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void task1() {
        LogUtil.info("定时任务[task1]开始执行");
        bondService.initTask();
        LogUtil.info("定时任务[task1]执行结束");

    }

    /**
     * 每小时整点分析一次数据
     */
    @Scheduled(cron = "0 */60 * * * ?")
    public void task2() {
        LogUtil.info("定时任务[task2]开始执行");
        try {
        } catch (Exception e) {
            LogUtil.error("定时任务[analysisData_ID_40]异常!", e);
        }

        LogUtil.info("定时任务[task2]执行结束");
    }

    /**
     * 半小时分析一次数据
     */
    @Scheduled(cron = "0 */30 * * * ?")
    public void analysisDataForHalfAnHour() {
        LogUtil.info("定时任务[analysisDataForHalfAnHour]开始执行");

        LogUtil.info("定时任务[analysisDataForHalfAnHour]执行结束");
    }


    /**
     * 每天0点处理一次数据分析任务
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void analysisDataForDayZero() {
        LogUtil.info("定时任务[analysisDataForDayZero]开始执行");
        LogUtil.info("定时任务[analysisDataForDayZero]执行结束");
    }
}

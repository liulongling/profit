package com.profit.base.domain;

import lombok.Data;

import java.util.Date;

@Data
public class TimeTaskLog {
    /**
     * 主键
     */
    private Long incId;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 同步时间
     */
    private Date syncTime;

    /**
     * 同步数量
     */
    private Integer syncCount;

    /**
     * 同步状态1:成功，0:失败
     */
    private Integer syncFlag;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
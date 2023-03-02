package com.profit.base.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class OperatingLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    private String id;

    /**
     * 操作方法
     */
    private String operMethod;

    /**
     * 操作用户
     */
    private String operUser;

    /**
     * 操作类型
     */
    private String operType;

    /**
     * 操作模块
     */
    private String operModule;

    /**
     * 操作标题
     */
    private String operTitle;

    /**
     * 操作路径
     */
    private String operPath;

    /**
     * 操作时间
     */
    private Long operTime;
}
package com.profit.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 一条折线
 */
@Data
@Accessors(chain = true)
public class EChartsElement {
    /**
     * 标题
     */
    private String name;
    /**
     * ECharts 图表类型 line 折线图
     */
    private String type;
    /**
     *
     */
    private String stack;
    /**
     * 数据
     */
    private List<?> data;
}

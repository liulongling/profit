package com.profit.commons.utils;

import java.util.List;

/**
 * 分页存放工具类
 *
 * @author xiaotao
 */
public class PageUtils<T> {

    /**
     * 数据总数
     */
    private long total;

    /**
     * 数据
     */
    private List<T> rows;

    public PageUtils(long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageUtils{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }

}

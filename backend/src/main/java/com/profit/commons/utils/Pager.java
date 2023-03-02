package com.profit.commons.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author:liulongling
 * @Date:2022/3/11 9:15
 */
@Getter
@Setter
public class Pager<T> {
    private T listObject;
    private long itemCount;
    private long pageCount;

    public Pager() {
    }

    public Pager(T listObject, long itemCount, long pageCount) {
        this.listObject = listObject;
        this.itemCount = itemCount;
        this.pageCount = pageCount;
    }

}

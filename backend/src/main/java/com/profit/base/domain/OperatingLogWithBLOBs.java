package com.profit.base.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OperatingLogWithBLOBs extends OperatingLog implements Serializable {
    /**
     * 操作内容
     */
    private String operContent;

    /**
     * 操作参数
     */
    private String operParams;

    private static final long serialVersionUID = 1L;
}
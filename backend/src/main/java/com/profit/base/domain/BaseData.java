package com.profit.base.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseData implements Serializable {
    private Integer id;

    private String description;

    private Long operTime;

    private String data;

    private static final long serialVersionUID = 1L;
}
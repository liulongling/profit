package com.profit.base.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SyUser implements Serializable {
    private String id;

    private String loginName;

    private String password;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
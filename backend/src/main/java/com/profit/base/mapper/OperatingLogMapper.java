package com.profit.base.mapper;

import com.profit.base.domain.OperatingLogWithBLOBs;


public interface OperatingLogMapper {

    int insert(OperatingLogWithBLOBs record);

}
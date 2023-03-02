package com.profit.log.service;

import com.profit.base.domain.OperatingLogWithBLOBs;
import com.profit.base.mapper.OperatingLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional(rollbackFor = Exception.class)
public class OperatingLogService {
    @Resource
    private OperatingLogMapper operatingLogMapper;

    public void create(OperatingLogWithBLOBs log) {
        operatingLogMapper.insert(log);
    }

}
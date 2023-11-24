package com.profit.base.mapper;

import com.profit.base.domain.TimeTaskLog;
import com.profit.base.domain.TimeTaskLogExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TimeTaskLogMapper {
    long countByExample(TimeTaskLogExample example);

    int deleteByExample(TimeTaskLogExample example);

    int deleteByPrimaryKey(Long incId);

    int insert(TimeTaskLog record);

    int insertSelective(TimeTaskLog record);

    List<TimeTaskLog> selectByExample(TimeTaskLogExample example);

    TimeTaskLog selectByPrimaryKey(Long incId);

    int updateByExampleSelective(@Param("record") TimeTaskLog record, @Param("example") TimeTaskLogExample example);

    int updateByExample(@Param("record") TimeTaskLog record, @Param("example") TimeTaskLogExample example);

    int updateByPrimaryKeySelective(TimeTaskLog record);

    int updateByPrimaryKey(TimeTaskLog record);
}
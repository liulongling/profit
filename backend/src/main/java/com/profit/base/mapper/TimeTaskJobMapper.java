package com.profit.base.mapper;

import com.profit.base.domain.TimeTaskJob;
import com.profit.base.domain.TimeTaskJobExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TimeTaskJobMapper {
    long countByExample(TimeTaskJobExample example);

    int deleteByExample(TimeTaskJobExample example);

    int deleteByPrimaryKey(Long incId);

    int insert(TimeTaskJob record);

    int insertSelective(TimeTaskJob record);

    List<TimeTaskJob> selectByExample(TimeTaskJobExample example);

    TimeTaskJob selectByPrimaryKey(Long incId);

    int updateByExampleSelective(@Param("record") TimeTaskJob record, @Param("example") TimeTaskJobExample example);

    int updateByExample(@Param("record") TimeTaskJob record, @Param("example") TimeTaskJobExample example);

    int updateByPrimaryKeySelective(TimeTaskJob record);

    int updateByPrimaryKey(TimeTaskJob record);
}
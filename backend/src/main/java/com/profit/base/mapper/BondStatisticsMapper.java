package com.profit.base.mapper;

import com.profit.base.domain.BondStatistics;
import com.profit.base.domain.BondStatisticsExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BondStatisticsMapper {
    long countByExample(BondStatisticsExample example);

    int deleteByExample(BondStatisticsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(BondStatistics record);

    int insertSelective(BondStatistics record);

    List<BondStatistics> selectByExample(BondStatisticsExample example);

    BondStatistics selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") BondStatistics record, @Param("example") BondStatisticsExample example);

    int updateByExample(@Param("record") BondStatistics record, @Param("example") BondStatisticsExample example);

    int updateByPrimaryKeySelective(BondStatistics record);

    int updateByPrimaryKey(BondStatistics record);
}
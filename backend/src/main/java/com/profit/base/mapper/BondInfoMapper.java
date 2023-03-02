package com.profit.base.mapper;

import com.profit.base.domain.BondInfo;
import com.profit.base.domain.BondInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BondInfoMapper {
    long countByExample(BondInfoExample example);

    int deleteByExample(BondInfoExample example);

    int deleteByPrimaryKey(String id);

    int insert(BondInfo record);

    int insertSelective(BondInfo record);

    List<BondInfo> selectByExample(BondInfoExample example);

    BondInfo selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") BondInfo record, @Param("example") BondInfoExample example);

    int updateByExample(@Param("record") BondInfo record, @Param("example") BondInfoExample example);

    int updateByPrimaryKeySelective(BondInfo record);

    int updateByPrimaryKey(BondInfo record);
}
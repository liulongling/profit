package com.profit.base.mapper;

import com.profit.base.domain.BondSellLog;
import com.profit.base.domain.BondSellLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BondSellLogMapper {
    long countByExample(BondSellLogExample example);

    int deleteByExample(BondSellLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(BondSellLog record);

    int insertSelective(BondSellLog record);

    List<BondSellLog> selectByExample(BondSellLogExample example);

    BondSellLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") BondSellLog record, @Param("example") BondSellLogExample example);

    int updateByExample(@Param("record") BondSellLog record, @Param("example") BondSellLogExample example);

    int updateByPrimaryKeySelective(BondSellLog record);

    int updateByPrimaryKey(BondSellLog record);
}
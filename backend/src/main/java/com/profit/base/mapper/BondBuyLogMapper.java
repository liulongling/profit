package com.profit.base.mapper;

import com.profit.base.domain.BondBuyLog;
import com.profit.base.domain.BondBuyLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BondBuyLogMapper {
    long countByExample(BondBuyLogExample example);

    int deleteByExample(BondBuyLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(BondBuyLog record);

    int insertSelective(BondBuyLog record);

    List<BondBuyLog> selectByExample(BondBuyLogExample example);

    BondBuyLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") BondBuyLog record, @Param("example") BondBuyLogExample example);

    int updateByExample(@Param("record") BondBuyLog record, @Param("example") BondBuyLogExample example);

    int updateByPrimaryKeySelective(BondBuyLog record);

    int updateByPrimaryKey(BondBuyLog record);
}
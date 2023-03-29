package com.profit.base.mapper;

import com.profit.base.domain.BaseData;
import com.profit.base.domain.BaseDataExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseDataMapper {
    long countByExample(BaseDataExample example);

    int deleteByExample(BaseDataExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BaseData record);

    int insertSelective(BaseData record);

    List<BaseData> selectByExampleWithBLOBs(BaseDataExample example);

    List<BaseData> selectByExample(BaseDataExample example);

    BaseData selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BaseData record, @Param("example") BaseDataExample example);

    int updateByExampleWithBLOBs(@Param("record") BaseData record, @Param("example") BaseDataExample example);

    int updateByExample(@Param("record") BaseData record, @Param("example") BaseDataExample example);

    int updateByPrimaryKeySelective(BaseData record);

    int updateByPrimaryKeyWithBLOBs(BaseData record);

    int updateByPrimaryKey(BaseData record);
}
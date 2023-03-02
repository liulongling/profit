package com.profit.base.mapper;

import com.profit.base.domain.SyUser;
import com.profit.base.domain.SyUserExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SyUserMapper {
    long countByExample(SyUserExample example);

    int deleteByExample(SyUserExample example);

    int deleteByPrimaryKey(String id);

    int insert(SyUser record);

    int insertSelective(SyUser record);

    List<SyUser> selectByExample(SyUserExample example);

    SyUser selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SyUser record, @Param("example") SyUserExample example);

    int updateByExample(@Param("record") SyUser record, @Param("example") SyUserExample example);

    int updateByPrimaryKeySelective(SyUser record);

    int updateByPrimaryKey(SyUser record);
}
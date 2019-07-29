package com.zhao.dao.mapper;

import com.zhao.dao.domain.Parent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ParentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Parent record);

    Parent selectByPrimaryKey(Integer id);

    List<Parent> selectAll();

    int updateByPrimaryKey(Parent record);

    int insertBatch(@Param("list") List<Parent> departList);

}
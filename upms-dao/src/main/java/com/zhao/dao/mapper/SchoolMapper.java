package com.zhao.dao.mapper;

import com.zhao.dao.domain.School;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SchoolMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(School record);

    School selectByPrimaryKey(Integer id);

    List<School> selectAll();

    int updateByPrimaryKey(School record);

    int insertBatch(@Param("list") List<School> departList);

}
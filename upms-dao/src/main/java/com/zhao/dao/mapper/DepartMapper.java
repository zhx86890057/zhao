package com.zhao.dao.mapper;

import com.zhao.dao.domain.Depart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Depart record);

    Depart selectByPrimaryKey(Integer id);

    List<Depart> selectAll();

    int updateByPrimaryKey(Depart record);

    int insertBatch(@Param("list") List<Depart> departList);
}
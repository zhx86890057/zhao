package com.zhao.dao.mapper;

import com.zhao.dao.domain.SysRolePermissionRelation;
import java.util.List;

public interface SysRolePermissionRelationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRolePermissionRelation record);

    SysRolePermissionRelation selectByPrimaryKey(Integer id);

    List<SysRolePermissionRelation> selectAll();

    int updateByPrimaryKey(SysRolePermissionRelation record);
}
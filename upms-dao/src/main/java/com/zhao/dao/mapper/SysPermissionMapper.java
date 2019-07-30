package com.zhao.dao.mapper;

import com.zhao.dao.domain.SysPermission;
import com.zhao.dao.vo.PermissionVO;

import java.util.List;

public interface SysPermissionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysPermission record);

    SysPermission selectByPrimaryKey(Integer id);

    List<SysPermission> selectAll();

    int updateByPrimaryKey(SysPermission record);
}
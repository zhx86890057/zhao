package com.zhao.dao.mapper;

import com.zhao.dao.domain.SysRole;
import com.zhao.dao.vo.RoleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRole record);

    SysRole selectByPrimaryKey(Integer id);

    List<SysRole> selectAll();

    int updateByPrimaryKey(SysRole record);

    List<RoleVO> selectByPermissionId(@Param("list")List<Integer> list);

    List<RoleVO> selectByUserId(Long userId);
}
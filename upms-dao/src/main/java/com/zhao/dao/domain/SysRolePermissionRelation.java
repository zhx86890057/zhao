package com.zhao.dao.domain;

import java.io.Serializable;

public class SysRolePermissionRelation implements Serializable {
    private static final long serialVersionUID = -5891561004505248463L;
    private Integer id;

    private String roleId;

    private Integer permissionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }
}
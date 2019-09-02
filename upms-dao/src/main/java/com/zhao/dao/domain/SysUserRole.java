package com.zhao.dao.domain;

import lombok.Builder;

import java.io.Serializable;

@Builder
public class SysUserRole implements Serializable {
    private static final long serialVersionUID = 7291951749713396947L;
    private Long id;

    private Long userId;

    private String roleId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
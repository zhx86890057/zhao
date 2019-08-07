package com.zhao.upms.web.security;

import com.zhao.dao.domain.SysPermission;
import com.zhao.dao.domain.SysUser;
import com.zhao.dao.vo.RoleVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SpringSecurity需要的用户详情
 */
public class SysUserDetails implements UserDetails {
    private SysUser sysUser;
    private List<RoleVO> roleVOList;

    public SysUserDetails(SysUser sysUser,List<RoleVO> roleVOList) {
        this.sysUser = sysUser;
        this.roleVOList = roleVOList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //返回当前用户的权限
        return roleVOList.stream().map(s -> new SimpleGrantedAuthority("ROLE_" + s.getId())).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return sysUser.getPassword();
    }

    @Override
    public String getUsername() {
        return sysUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return sysUser.getStatus().equals(1);
    }
}

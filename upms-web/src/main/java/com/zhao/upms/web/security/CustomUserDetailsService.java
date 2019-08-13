package com.zhao.upms.web.security;

import com.zhao.dao.domain.SysUser;
import com.zhao.dao.mapper.SysRoleMapper;
import com.zhao.dao.mapper.SysUserMapper;
import com.zhao.dao.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 提供到数据库查询该用户的权限信息
        // 关于角色和权限的转换关系在此处处理，根据用户与角色的关系、角色与权限的关系，
        // 将用户与权限的管理整理出来
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
        List<RoleVO> roleVOList = sysRoleMapper.selectByUserId(user.getId());

        return new SysUserDetails(user, roleVOList);
    }
}

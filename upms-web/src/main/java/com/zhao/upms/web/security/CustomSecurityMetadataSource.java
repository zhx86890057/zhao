package com.zhao.upms.web.security;

import com.zhao.dao.domain.SysPermission;
import com.zhao.dao.mapper.SysPermissionMapper;
import com.zhao.dao.mapper.SysRoleMapper;
import com.zhao.dao.vo.PermissionVO;
import com.zhao.dao.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private Map<String, List<ConfigAttribute>> resources;
    private SysPermissionMapper sysPermissionMapper;
    private SysRoleMapper sysRoleMapper;

    public CustomSecurityMetadataSource(SysPermissionMapper sysPermissionMapper, SysRoleMapper sysRoleMapper) {
        this.sysPermissionMapper = sysPermissionMapper;
        this.sysRoleMapper = sysRoleMapper;
        loadAuthorityResources();
    }

    private void loadAuthorityResources() {
        // 此处在创建时从数据库中初始化权限数据
        // 将权限与资源数据整理成 Map<resource, List<Authority>> 的形式
        // 注意：加载URL资源时，需要对资源进行排序，要由精确到粗略进行排序，让精确的URL优先匹配,在数据库中排序
        resources = new LinkedHashMap<>();
        List<SysPermission> permissionList = sysPermissionMapper.selectAll();
        if(!CollectionUtils.isEmpty(permissionList)){
            List<Integer> permissionIdList = permissionList.stream().map(s -> s.getId()).collect(Collectors.toList());
            List<RoleVO> roleVOList = sysRoleMapper.selectByPermissionId(permissionIdList);
            if(!CollectionUtils.isEmpty(roleVOList)){
                Map<Integer, List<RoleVO>> map = roleVOList.stream().collect(Collectors.groupingBy(RoleVO::getPermissionId));
                permissionList.forEach(s -> {
                    Integer id = s.getId();
                    if(map.containsKey(id)){
                        List<RoleVO> roleVOS = map.get(id);
                        List<ConfigAttribute> authorityList = roleVOS.stream().map(r -> new SecurityConfig(r.getId())).collect(Collectors.toList());
                        resources.put(s.getUrl(),authorityList);
                    }
                });
            }
        }
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        FilterInvocation fi = (FilterInvocation) object;
        for (Map.Entry<String, List<ConfigAttribute>> entry : resources.entrySet()) {
            String uri = entry.getKey();
            RequestMatcher requestMatcher = new AntPathRequestMatcher(uri);
            if (requestMatcher.matches(fi.getHttpRequest())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

}

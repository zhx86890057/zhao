package com.zhao.dao.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RoleVO {
    private Integer id;

    private String name;

    private String description;

    private Date createTime;

    private Date modifyTime;

    private Integer permissionId;

    private Long userId;
}

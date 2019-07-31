package com.zhao.dao.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO {
    private Integer id;

    private String name;

    private String description;

    private Date createTime;

    private Date modifyTime;

    private Integer permissionId;

    private Long userId;
}

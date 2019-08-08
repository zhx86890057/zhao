package com.zhao.dao.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO implements Serializable {
    private static final long serialVersionUID = -8035320484595522820L;
    private String id;

    private String description;

    private Date createTime;

    private Date modifyTime;

    private Integer permissionId;

    private Long userId;
}

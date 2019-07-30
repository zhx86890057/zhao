package com.zhao.dao.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PermissionVO{

    private Integer id;

    private String url;

    private String name;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    private Date createTime;

    private Date modifyTime;

    @ApiModelProperty(value = "角色名称")
    private String roleName;
}

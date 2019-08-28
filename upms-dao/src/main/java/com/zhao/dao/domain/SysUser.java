package com.zhao.dao.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysUser implements Serializable {

    private static final long serialVersionUID = 3661917820445791077L;
    private Long id;
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;

    private String email;

    private String phone;

    private String name;

    private Date lastLoginTime;

    private Integer status;

    private Date createTime;

    private Date modifyTime;
}
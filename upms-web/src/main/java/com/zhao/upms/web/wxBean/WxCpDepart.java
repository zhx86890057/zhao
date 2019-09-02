package com.zhao.upms.web.wxBean;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信部门.
 *
 * @author Daniel Qian
 */
@Data
public class WxCpDepart implements Serializable {

  private static final long serialVersionUID = -5028321625140879571L;
  private Long id;
  private String name;
  private Long parentId;
  private Long order;

}

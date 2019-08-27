package com.zhao.upms.web.wxBean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxAccessToken {

  private String accessToken;

  private int expiresIn = -1;

}

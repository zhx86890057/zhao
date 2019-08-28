package com.zhao.upms.web.wxBean;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

/**
 * 授权方企业信息
 *
 */
@Data
public class WxAuthCorpInfo{
  @JSONField(name = "corpid")
  @ApiModelProperty("授权方企业微信id")
  private String corpId;

  @JSONField(name ="corp_name")
  @ApiModelProperty("授权方企业名称")
  private String corpName;

  @JSONField(name ="corp_type")
  @ApiModelProperty("授权方企业类型，认证号：verified, 注册号：unverified")
  private String corpType;

  @JSONField(name ="corp_square_logo_url")
  @ApiModelProperty("授权方企业方形头像")
  private String corpSquareLogoUrl;

  @JSONField(name ="corp_user_max")
  @ApiModelProperty("授权方企业用户规模")
  private Integer corpUserMax;

  @JSONField(name ="corp_full_name")
  @ApiModelProperty("授权方企业的主体名称(仅认证或验证过的企业有)")
  private String corpFullName;

  @JSONField(name ="subject_type")
  @ApiModelProperty("企业类型，1. 企业; 2. 政府以及事业单位; 3. 其他组织, 4.团队号")
  private Integer subjectType;

  @JSONField(name ="verified_end_time")
  @ApiModelProperty("认证到期时间")
  private Date verifiedEndTime;

  @JSONField(name ="corp_wxqrcode")
  @ApiModelProperty("授权企业在微工作台（原企业号）的二维码，可用于关注微工作台")
  private String corpWxqrcode;

  @JSONField(name ="corp_scale")
  @ApiModelProperty("企业规模。当企业未设置该属性时，值为空")
  private String corpScale;

  @JSONField(name ="corp_industry")
  @ApiModelProperty("企业所属行业。当企业未设置该属性时，值为空")
  private String corpIndustry;

  @JSONField(name ="corp_sub_industry")
  @ApiModelProperty("企业所属子行业。当企业未设置该属性时，值为空")
  private String corpSubIndustry;

  @JSONField(name ="location")
  @ApiModelProperty("企业所在地信息, 为空时表示未知")
  private String location;

  @JSONField(name ="permanent_code")
  @ApiModelProperty("永久授权码")
  private String permanentCode;

  @JSONField(name ="errcode")
  @ApiModelProperty("错误码")
  private Integer errcode;

}

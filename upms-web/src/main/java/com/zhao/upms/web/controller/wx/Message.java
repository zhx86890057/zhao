package com.zhao.upms.web.controller.wx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业微信开放了消息发送接口，企业可以使用这些接口让自定义应用与企业微信后台或用户间进行双向通信。
 *
 * @author wison
 */
@RestController
@Slf4j
@RequestMapping(value = "/wx/message")
public class Message {

}

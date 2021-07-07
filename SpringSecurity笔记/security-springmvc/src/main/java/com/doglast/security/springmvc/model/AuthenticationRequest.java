package com.doglast.security.springmvc.model;

import lombok.Data;

/**
 * @author Administrator
 * @version 1.0
 * 封装认证的请求信息
 **/
@Data
public class AuthenticationRequest {
    //认证请求参数，账号、密码。。
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}

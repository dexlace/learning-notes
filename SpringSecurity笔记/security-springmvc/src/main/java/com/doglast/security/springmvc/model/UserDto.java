package com.doglast.security.springmvc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * @author Administrator
 * @version 1.0
 * 封装用户的信息
 **/
@Data
@AllArgsConstructor
public class UserDto {

    // 首先在UserDto中定义一个SESSION_USER_KEY，作为Session中存放登录用户信息的key。
    public static final String SESSION_USER_KEY = "_user";
    //用户身份信息
    private String id;
    private String username;
    private String password;
    private String fullname;
    private String mobile;
    /**
     * 用户权限
     */
    private Set<String> authorities;
}

package com.security.distributed.order.controller;

import com.security.distributed.order.model.UserDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/4/19
 */
@RestController
public class OrderController {
    @GetMapping(value = "/r/r1")
    @PreAuthorize("hasAnyAuthority('p1')")
    public String r1() {
        UserDTO user = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  user.getUsername()+"访问资源1";
    }

    @GetMapping(value = "/r/r2")
    @PreAuthorize("hasAnyAuthority('p2')")
    public String r2() {
        UserDTO user = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  user.getUsername()+"访问资源2";
    }


}

package com.unict.dieei.pr20.videomanagementservice.controller;

import com.unict.dieei.pr20.videomanagementservice.entity.User;
import com.unict.dieei.pr20.videomanagementservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(path = "/register")
    public @ResponseBody User register(@RequestBody User user) {
        return userService.addUser(user);
    }
}

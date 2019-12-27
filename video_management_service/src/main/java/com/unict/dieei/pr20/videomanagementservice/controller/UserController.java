package com.unict.dieei.pr20.videomanagementservice.controller;

import com.unict.dieei.pr20.videomanagementservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    UserService userService;
}

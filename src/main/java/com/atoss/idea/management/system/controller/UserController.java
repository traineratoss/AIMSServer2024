package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
}

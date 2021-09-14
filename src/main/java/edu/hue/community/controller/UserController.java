package edu.hue.community.controller;

import edu.hue.community.entity.User;
import edu.hue.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 47552
 * @date 2021/09/12
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUser/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        return null;
    }

}

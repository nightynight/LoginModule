package com.brokepal.controller;

import com.brokepal.entity.User;
import com.brokepal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by Administrator on 2016/11/3.
 */
@Controller
@RequestMapping("/static/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{username}")
    @ResponseBody
    public ResponseEntity<User> getUser(@PathVariable("username") String username) throws IOException {
        return new ResponseEntity(userService.getUserByUsername(username), HttpStatus.OK);
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<User> updateUser(
            @RequestParam("nickname") String nickname,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone
            ) throws IOException {
        return new ResponseEntity(userService.updateUser(new User(nickname,username,password,email,phone)), HttpStatus.OK);
    }

    @RequestMapping(value = "/create",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<User> createUser(
            @RequestParam("nickname") String nickname,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone
    ) throws IOException {
        return new ResponseEntity(userService.createUser(new User(nickname,username,password,email,phone)), HttpStatus.OK);
    }

}

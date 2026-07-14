package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    // 登录
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> res = new HashMap<>();
        User dbUser = userMapper.login(user.getUsername(), user.getPassword());
        if (dbUser != null) {
            res.put("code", 200);
            res.put("msg", "登录成功");
            res.put("data", dbUser); // 把用户信息返回给前端存起来
        } else {
            res.put("code", 400);
            res.put("msg", "账号或密码错误");
        }
        return res;
    }

    // 注册
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> res = new HashMap<>();
        // 先查是否存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            res.put("code", 400);
            res.put("msg", "用户名已存在");
            return res;
        }
        userMapper.register(user);
        res.put("code", 200);
        res.put("msg", "注册成功");
        return res;
    }

    // 刷新用户信息 (用于交易后同步余额)
    @GetMapping("/info/{id}")
    public Map<String, Object> getUserInfo(@PathVariable Long id) {
        User user = userMapper.findById(id);
        Map<String, Object> res = new HashMap<>();
        if (user != null) {
            res.put("code", 200);
            res.put("data", user);
        } else {
            res.put("code", 404);
        }
        return res;
    }
}
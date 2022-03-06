package com.ly.miaosha.controller;

import com.ly.miaosha.domain.User;
import com.ly.miaosha.rabbitmq.MQSender;
import com.ly.miaosha.redis.RedisService;
import com.ly.miaosha.redis.UserKey;
import com.ly.miaosha.result.Result;
import com.ly.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sample")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
        String msg = "hello, ly!";
        sender.send(msg);
        return Result.success(msg);
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(UserKey.getById, "" + 1, User.class); // UserKey:id1
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user = new User(1L, "liyang");
        redisService.set(UserKey.getById, "" + 1, user); // UserKey:id1
        return Result.success(true);
    }
}

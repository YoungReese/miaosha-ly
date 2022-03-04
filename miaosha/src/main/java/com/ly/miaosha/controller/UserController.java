package com.ly.miaosha.controller;

import com.ly.miaosha.domain.MiaoshaUser;
import com.ly.miaosha.redis.RedisService;
import com.ly.miaosha.result.CodeMsg;
import com.ly.miaosha.result.Result;
import com.ly.miaosha.service.MiaoshaUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 获取用户信息，用于 JMeter 压测
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
//        log.info(user.toString());
        return Result.success(user);
    }

}

package com.ly.miaosha.service;

import com.ly.miaosha.controller.LoginController;
import com.ly.miaosha.controller.UserController;
import com.ly.miaosha.dao.MiaoshaUserDao;
import com.ly.miaosha.domain.MiaoshaUser;
import com.ly.miaosha.exception.GlobalException;
import com.ly.miaosha.redis.MiaoshaUserKey;
import com.ly.miaosha.redis.RedisService;
import com.ly.miaosha.result.CodeMsg;
import com.ly.miaosha.util.MD5Util;
import com.ly.miaosha.util.UUIDUtil;
import com.ly.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    private static Logger log = LoggerFactory.getLogger(MiaoshaUserService.class);
    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    /**
     * 缓存对象
     */
    public MiaoshaUser getById(long id) {
        // 取缓存
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if (miaoshaUser != null) {
            return miaoshaUser;
        }
        miaoshaUser = miaoshaUserDao.getById(id);
        if (miaoshaUser != null) {
            redisService.set(MiaoshaUserKey.getById, "" + id, miaoshaUser);
        }
        return miaoshaUser;
    }

    // http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
    public boolean updatePassword(String token, long id, String formPass) {
        // 取 miaoshaUser
        MiaoshaUser miaoshaUser = getById(id);
        if (miaoshaUser == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // 更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser(); // 这里创建新对象是为了提高性能，直接更新原 miaoshaUser 会产生很多 binlog
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, miaoshaUser.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        // 处理缓存
        redisService.delete(MiaoshaUserKey.getById, "" + id);
        miaoshaUser.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token, miaoshaUser);
        return true;
    }

    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        // 延长有效期
        if (user != null) {
//            log.info("延长用户 token 有效期，token = {}", token);
            addCookie(response, token, user);
        }
        return user;
    }


    public String login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        // 判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // 验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        // 生成 token 存储到 cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;
    }

    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        redisService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds()); // 设置 cookie 有效期
        cookie.setPath("/"); // /catalog, which makes the cookie visible to all directories on the server under /catalog.
        response.addCookie(cookie);
    }

}

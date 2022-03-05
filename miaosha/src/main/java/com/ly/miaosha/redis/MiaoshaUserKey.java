package com.ly.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix {

    public static final int TOKEN_EXPIRE = 3600 * 24 * 2; // 2 天

    private MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE, "tk"); // tk: token
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id"); // 这里 0 表示永不过期
}

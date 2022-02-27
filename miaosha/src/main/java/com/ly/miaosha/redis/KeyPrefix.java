package com.ly.miaosha.redis;

public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();
}

package com.ly.miaosha.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisConfig {
    private String host;
    private int port;
    private int timeout;// 单位：秒，系统使用毫秒，所以配置中乘以 1000
    private String password;
    private int poolMaxTotal;
    private int poolMaxIdle;
    private int poolMaxWait;
//    public String getHost() {
//        return host;
//    }
//    public void setHost(String host) {
//        this.host = host;
//    }
//    public int getPort() {
//        return port;
//    }
//    public void setPort(int port) {
//        this.port = port;
//    }
//    public int getTimeout() {
//        return timeout;
//    }
//    public void setTimeout(int timeout) {
//        this.timeout = timeout;
//    }
//    public String getPassword() {
//        return password;
//    }
//    public void setPassword(String password) {
//        this.password = password;
//    }
//    public int getPoolMaxTotal() {
//        return poolMaxTotal;
//    }
//    public void setPoolMaxTotal(int poolMaxTotal) {
//        this.poolMaxTotal = poolMaxTotal;
//    }
//    public int getPoolMaxIdle() {
//        return poolMaxIdle;
//    }
//    public void setPoolMaxIdle(int poolMaxIdle) {
//        this.poolMaxIdle = poolMaxIdle;
//    }
//    public int getPoolMaxWait() {
//        return poolMaxWait;
//    }
//    public void setPoolMaxWait(int poolMaxWait) {
//        this.poolMaxWait = poolMaxWait;
//    }
}

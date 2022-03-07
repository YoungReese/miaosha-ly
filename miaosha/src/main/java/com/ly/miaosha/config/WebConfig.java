package com.ly.miaosha.config;

import com.ly.miaosha.access.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * liyang 2022-03-08
 * 经过 debug ，拦截器中的 preHandle 方法会先于 resolveArgument 执行
 * todo：因此 resolveArgument 方法中的部分代码可以提取到 preHandle 中
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    UserArgumentResolver userArgumentResolver;

    @Autowired
    AccessInterceptor accessInterceptor;

	/**
	 * springmvc 会调用这个方法把参数设置进去
	 * 由于是定制化，只需要加我们需要的参数即可
	 */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver); // 把这个参数追加上
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
    }
}

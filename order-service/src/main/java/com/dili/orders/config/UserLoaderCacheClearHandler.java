package com.dili.orders.config;

import com.dili.orders.service.component.impl.UserLoaderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserLoaderCacheClearHandler implements HandlerInterceptor {

    @Autowired
    private UserLoaderImpl userLoader;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        this.userLoader.clearCache();
    }
}

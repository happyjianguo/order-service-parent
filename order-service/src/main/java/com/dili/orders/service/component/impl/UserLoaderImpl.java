package com.dili.orders.service.component.impl;

import com.dili.orders.service.component.UserLoader;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.AppException;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.UserRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserLoaderImpl implements UserLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoaderImpl.class);

    //线程缓存，避免同一次请求多次RPC请求
    private static final ThreadLocal<Map<Long, User>> CACHE = new ThreadLocal<>();

    @Autowired
    private UserRpc userRpc;

    @Override
    public User getById(Long userId) {
        User user = null;
        Map<Long, User> cache = CACHE.get();
        if (cache == null) {
            cache = new HashMap<>();
            CACHE.set(cache);
        }
        user = cache.get(userId);
        if (user == null) {
            LOGGER.debug("开始调用UAP系统查询操作员信息，操作员id：{}", userId);
            long start = System.currentTimeMillis();
            BaseOutput<User> output = this.userRpc.get(userId);
            long end = System.currentTimeMillis();
            LOGGER.debug("结束调用UAP系统查询操作员信息，操作员id：{}，耗时{}毫秒", userId, end - start);
            if (!output.isSuccess()) {
                throw new AppException(output.getMessage());
            }
            user = output.getData();
            cache.put(userId, user);
        }
        return user;
    }

    public void clearCache() {
        CACHE.remove();
    }
}

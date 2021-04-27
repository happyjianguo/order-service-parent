package com.dili.orders.service.component;

import com.dili.uap.sdk.domain.User;

/**
 * 用户查询类
 */
public interface UserLoader {

    /**
     * 根据用户id查询用户
     *
     * @param userId
     * @return
     */
    User getById(Long userId);
}

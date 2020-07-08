package com.j.openproject.websocket.topic;

import java.util.Set;

/**
 * @author Joyuce
 * @Type UserTopicOperations
 * @Desc 用户订阅操作
 * @date 2019年11月07日
 * @Version V1.0
 */
public interface UserTopicOperations {

    /**
     * 添加订阅
     *
     * @param userId
     * @param topic
     */
    public void addUserByTopic(Integer userId, String topic);

    /**
     * 移除订阅
     *
     * @param userId
     * @param topic
     */
    public void removeUserByTopic(Integer userId, String topic);

    /**
     * 获取某类订阅的用户
     *
     * @param topic
     * @return
     */
    public Set<Integer> getUserSetByTopic(String topic);
}

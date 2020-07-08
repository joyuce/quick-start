package com.j.openproject.websocket.topic.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.j.openproject.websocket.topic.UserTopicOperations;

/**
 * @author Joyuce
 * @Type UserTopic
 * @Desc 用户订阅数据类
 * @date 2019年11月07日
 * @Version V1.0
 */
@Component
public class UserTopicOperationsImpl implements UserTopicOperations {

    /***
     * 订阅的主题 关联用户集合；实现可以替换为db或redis缓存
     *
     */
    private final Map<String, Set<Integer>> userTopicMap = new ConcurrentHashMap<>();

    /**
     * 订阅（未持久化）
     *
     * @param userId
     * @param topic
     */
    @Override
    public void addUserByTopic(Integer userId, String topic) {
        if (StringUtils.isNotBlank(topic)) {
            Set<Integer> userSet = getUserSetByTopic(topic);
            userSet.add(userId);
        }
    }

    @Override
    public void removeUserByTopic(Integer userId, String topic) {
        if (StringUtils.isNotBlank(topic)) {
            Set<Integer> userSet = getUserSetByTopic(topic);
            userSet.remove(userId);
        }
    }

    @Override
    public Set<Integer> getUserSetByTopic(String topic) {
        return userTopicMap.computeIfAbsent(topic, k -> new HashSet<>());
    }

}

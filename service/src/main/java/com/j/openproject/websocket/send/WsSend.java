package com.j.openproject.websocket.send;

import java.util.HashSet;
import java.util.Set;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.j.openproject.mq.MQConfig;
import com.j.openproject.websocket.entity.PushMessage;
import com.j.openproject.websocket.entity.WebsocketMQMessage;
import com.j.openproject.websocket.topic.UserTopicOperations;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type WsSend
 * @Desc websocket内部消息发送类  静态发消息方法
 * @date 2019年11月07日
 * @Version V1.0
 */
@Slf4j
@Component
public class WsSend {

    private static UserTopicOperations userTopicOperations;

    @Autowired
    public void setUserTopicOperations(UserTopicOperations userTopicOperations) {
        WsSend.userTopicOperations = userTopicOperations;
    }

    private static RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        WsSend.rabbitTemplate = rabbitTemplate;
    }

    private static void convertAndSend(WebsocketMQMessage mqMessage) {
        rabbitTemplate.convertAndSend(MQConfig.websocketFanoutExchange, "", mqMessage);
    }

    /**
     * 推送，排除某一些用户
     *
     * @param message
     * @param ids
     */
    public static void publishMessageToAllExcludeUsers(PushMessage message, Set<Integer> ids) {
        WebsocketMQMessage mqMessage = new WebsocketMQMessage(WebsocketMQMessage.EX_SEND, ids, message);
        convertAndSend(mqMessage);
    }

    /**
     * 推送，排除某一用户
     *
     * @param message
     * @param userId
     */
    public static void publishMessageToAllExcludeUser(PushMessage message, Integer userId) {
        Set<Integer> userIdList = new HashSet<>();
        userIdList.add(userId);
        WebsocketMQMessage mqMessage = new WebsocketMQMessage(WebsocketMQMessage.EX_SEND, userIdList, message);
        convertAndSend(mqMessage);
    }

    /**
     * 推送给当前系统全部在线用户
     *
     * @param message
     */
    public static void publishMessageToAll(PushMessage message) {
        WebsocketMQMessage mqMessage = new WebsocketMQMessage(WebsocketMQMessage.ALL_SEND, null, message);
        convertAndSend(mqMessage);
    }

    /**
     * 发送给指定的几个用户
     *
     * @param message
     * @param userIds 用户数组
     */
    public static void publishMessageToUsers(PushMessage message, Set<Integer> userIds) {
        WebsocketMQMessage mqMessage = new WebsocketMQMessage(WebsocketMQMessage.SOME_SEND, userIds, message);
        convertAndSend(mqMessage);
    }

    /**
     * 发送给订阅的用户消息
     *
     * @param message
     * @param topic
     */
    public static void publishMessageByTopic(PushMessage message, String topic) {
        Set<Integer> userIds = userTopicOperations.getUserSetByTopic(topic);
        WebsocketMQMessage mqMessage = new WebsocketMQMessage(WebsocketMQMessage.SOME_SEND, userIds, message);
        convertAndSend(mqMessage);
    }

    /**
     * 给指定用户推送消息
     *
     * @param message message
     * @param userId  userId
     */
    public static void publishMessageToUser(PushMessage message, Integer userId) {
        Set<Integer> userIdList = new HashSet<>();
        userIdList.add(userId);
        WebsocketMQMessage mqMessage = new WebsocketMQMessage(WebsocketMQMessage.SOME_SEND, userIdList, message);
        convertAndSend(mqMessage);
    }

}

package com.j.openproject.mq;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.j.openproject.websocket.core.WebsocketCore;
import com.j.openproject.websocket.entity.PushMessage;
import com.j.openproject.websocket.entity.WebsocketMQMessage;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

/**
 * @author joyuce
 * @Type WebsocketListener
 * @Desc
 * @date 2019年06月24日
 * @Version V1.0
 */
@Slf4j
@Component
public class WebsocketListener implements ChannelAwareMessageListener {

    @Autowired
    private WebsocketCore websocketCore;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            String s = transformByteToString(message.getBody());
            log.debug("websocket消息监听，接收到一份数据 " + s);
            WebsocketMQMessage mqMessage = JSON.parseObject(s, WebsocketMQMessage.class);
            handler(mqMessage);
        } catch (Exception e) {
            log.error("错误了", e.getMessage());
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    private void handler(WebsocketMQMessage mqMessage) {
        if (mqMessage == null || mqMessage.getType() == null || mqMessage.getPushMessage() == null) {
            return;
        }
        PushMessage message = mqMessage.getPushMessage();
        Integer type = mqMessage.getType();
        switch (type) {
        case 0:
            websocketCore.publishMessageToUsersMQ(message, mqMessage.getUserIdList());
            break;
        case 1:
            websocketCore.publishMessageToAllExcludeUsersMQ(message, new HashSet<>(mqMessage.getUserIdList()));
            break;
        case 2:
            websocketCore.publishMessageToAllMQ(message);
            break;
        default:
            break;
        }
    }

    public static String transformByteToString(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, StandardCharsets.UTF_8);
    }

}

package com.j.openproject.websocket.handler;

import org.springframework.beans.factory.InitializingBean;

import com.j.openproject.websocket.entity.ReceiveMessage;

/**
 * @author joyuce
 * @Type HandleMessage
 * @Desc websocket 客户端消息处理
 * @date 2019年10月30日
 * @Version V1.0
 */
public interface WsMessageHandler extends InitializingBean {

    /**
     * 处理用户消息
     *
     * @param userId
     * @param msg
     */
    public void handlingMessage(Integer userId, ReceiveMessage msg);

}

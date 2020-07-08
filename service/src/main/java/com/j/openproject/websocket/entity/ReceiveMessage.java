package com.j.openproject.websocket.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * @author joyuce
 * @Type ReceiveMessage
 * @Desc 用户端消息类
 * @date 2019年10月29日
 * @Version V1.0
 */
@Setter
@Getter
public class ReceiveMessage implements Serializable {

    private static final long serialVersionUID = -1189432817967839050L;

    //消息类型
    private String messageType;
    //消息数据
    private Object messageData;
    //消息过期时间
    private int expireTime;

    public ReceiveMessage() {
    }
}

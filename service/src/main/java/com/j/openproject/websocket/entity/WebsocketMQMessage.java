package com.j.openproject.websocket.entity;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

/**
 * @author joyuce
 * @Type WebsocketMQMessage
 * @Desc
 * @date 2019年06月24日
 * @Version V1.0
 */
@Data
public class WebsocketMQMessage implements Serializable {

    private static final long serialVersionUID = -1682484510394976762L;
    //只对某些用户
    public static final Integer SOME_SEND = 0;
    //排除某些用户
    public static final Integer EX_SEND = 1;
    //推送全部用户
    public static final Integer ALL_SEND = 2;
    //消息体
    private PushMessage pushMessage;
    //消息类型（针对系统） 0 只对某些用户 1 排除某些用户 2 推送全部用户
    private Integer type;
    //用户id集合
    private Set<Integer> userIdList;

    public WebsocketMQMessage() {
    }

    public WebsocketMQMessage(Integer type, Set<Integer> userIdList, PushMessage pushMessage) {
        this.pushMessage = pushMessage;
        this.type = type;
        this.userIdList = userIdList;

    }
}

package com.j.openproject.websocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Joyuce
 * @Type MessageTypeEnum
 * @Desc 客户端发送的消息类型
 * @date 2019年11月07日
 * @Version V1.0
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    /** 客户端心跳处理 */
    HEARTBEAT("0", "heartbeat", "客户端心跳处理"),

    ;
    /** 发送消息类型 0 客户端消息  1 服务端消息 */
    private final String sendType;

    /** 客户端消息处理名称 */
    private final String messageType;

    /** 客户端消息处理详情 */
    private final String messageDetail;

}

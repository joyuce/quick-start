package com.j.openproject.websocket;

import java.io.IOException;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.j.openproject.config.EndpointConfigure;
import com.j.openproject.websocket.core.WebsocketCore;

import lombok.extern.slf4j.Slf4j;

/**
 * @author joyuce
 * @Type Websocket
 * @Desc ws连接类
 * @date 2019年06月05日
 * @Version V1.0
 */
@ServerEndpoint(value = "/websocket/{validParams}", configurator = EndpointConfigure.class)
@Component
@Slf4j
public class Websocket {

    @Autowired
    private WebsocketCore websocketCore;

    /**
     * 用户连接时触发
     *
     * @param session session
     */
    @OnOpen
    public void open(Session session, @PathParam("validParams") String validParams) throws IOException {
        websocketCore.increment();
        log.info(validParams);
        String[] vparams = validParams.split("-");
        if (StringUtils.isAllBlank(vparams)) {
            log.info("Websocket 末尾参数校验不通过");
            session.close();
            return;
        }
        String topic = null;

        Integer userId = 1;
        try {
            //todo 根据token获取用户id

        } catch (Exception e) {
            log.info("Websocket 获取不到userId");
            session.close();
            return;
        }
        if (userId == null) {
            log.info("Websocket 获取不到userId");
            session.close();
            return;
        }
        websocketCore.put(userId, session, topic);
        log.info("Websocket有连接接入，当前连接总数----->  " + websocketCore.getOnlineNum());
    }

    /**
     * 收到信息时触发
     *
     * @param message message
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        websocketCore.handlingUserMessage(message, session);
    }

    /**
     * 连接关闭触发
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        websocketCore.decrement();
        websocketCore.handlingSessionClose(session, closeReason);
    }

    /**
     * 发生错误时触发
     *
     * @param session session
     * @param error   error
     */
    @OnError
    public void onError(Session session, Throwable error) {
    }
}

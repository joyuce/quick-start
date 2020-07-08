package com.j.openproject.websocket.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.j.openproject.thread.FlexThreadPool;
import com.j.openproject.websocket.handler.factory.WsMessageHandleFactory;
import com.j.openproject.websocket.topic.UserTopicOperations;
import com.j.openproject.websocket.entity.PushMessage;
import com.j.openproject.websocket.entity.ReceiveMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * @author joyuce
 * @Type WebsocketUtil
 * @Desc
 * @date 2019年06月05日
 * @Version V1.0
 */
@Slf4j
@Component
public class WebsocketCore {

    @Autowired
    private WsMessageHandleFactory wsMessageHandleFactory;
    @Autowired
    private UserTopicOperations userTopicOperations;

    /***
     * 用户id关联session
     *
     */
    private final Map<Integer, Session> sessionPool = new ConcurrentHashMap<>();

    /**
     * 线程池
     */
    private final FlexThreadPool highThreadExecutor = new FlexThreadPool(8);

    private final FlexThreadPool lowThreadExecutor = new FlexThreadPool(8);

    private final Map<String, Integer> sessionUserIdMap = new ConcurrentHashMap<>();

    private final AtomicInteger countLink = new AtomicInteger(0);

    public void put(Integer userId, Session session, String topic) {
        sessionPool.put(userId, session);
        sessionUserIdMap.put(session.getId(), userId);
        userTopicOperations.addUserByTopic(userId, topic);
    }

    private Integer getUserIdBySessionId(String sessionId) {
        return sessionUserIdMap.get(sessionId);
    }

    private Session getSessionByUserId(Integer userId) {
        return sessionPool.get(userId);
    }

    public Map<Integer, Session> getPool() {
        return sessionPool;
    }

    public void clear() {
        sessionPool.clear();
        sessionUserIdMap.clear();
    }

    /**
     * MQ使用
     *
     * @param message
     */
    public void publishMessageToAllMQ(PushMessage message) {
        String str = JSONObject.toJSONString(message);
        boolean isCollect = message.isCollect();
        getPoolByLevel(message).execute(() -> {
            log.debug("消息开始异步群发");
            long pushNum = 0;
            for (Map.Entry<Integer, Session> entry : sessionPool.entrySet()) {
                if (pushMessage(str, entry.getValue(), isCollect)) {
                    pushNum++;
                }
            }
            log.debug("消息异步推送结束，共推送到的目标数量：" + pushNum + " 个");
        });
    }

    /**
     * MQ使用
     *
     * @param message
     * @param userId
     */
    public void publishMessageToAllExcludeUserMQ(PushMessage message, Integer userId) {
        Set<Integer> ids = new HashSet<>();
        ids.add(userId);
        publishMessageToAllExcludeUsersMQ(message, ids);
    }

    /**
     * Mq
     *
     * @param message
     * @param ids
     */
    public void publishMessageToAllExcludeUsersMQ(PushMessage message, Set<Integer> ids) {
        String str = JSONObject.toJSONString(message);
        boolean isCollect = message.isCollect();
        getPoolByLevel(message).execute(() -> {
            log.debug("消息开始异步群发");
            long pushNum = 0;
            for (Map.Entry<Integer, Session> entry : sessionPool.entrySet()) {
                if (!ids.contains(entry.getKey())) {
                    if (pushMessage(str, entry.getValue(), isCollect)) {
                        pushNum++;
                    }
                }

            }
            log.debug("消息异步推送结束，共推送到的目标数量：" + pushNum + " 个");
        });

    }

    public void publishMessageToUserMQ(PushMessage message, Integer userId) {
        log.debug("消息开始异步推送 用户id为：" + userId);
        ExecutorService threadExecutor = getPoolByLevel(message);
        String str = JSONObject.toJSONString(message);
        boolean isCollect = message.isCollect();
        threadExecutor.execute(() -> {
            pushMessageToUser(str, userId, isCollect);
        });
        log.debug("消息异步推送结束");
    }

    private ExecutorService getPoolByLevel(PushMessage message) {
        ExecutorService threadExecutor = null;
        if (message.getMessageLevel() == 0) {
            threadExecutor = highThreadExecutor;
        } else {
            threadExecutor = lowThreadExecutor;
        }
        return threadExecutor;
    }

    /**
     * 获取当前连接数
     *
     * @return int
     */
    public int getOnlineNum() {
        return countLink.get();
    }

    public void increment() {
        countLink.incrementAndGet();
    }

    public void decrement() {
        countLink.decrementAndGet();
    }

    public void publishMessageToUsersMQ(PushMessage message, Set<Integer> userIds) {
        for (Integer userId : userIds) {
            publishMessageToUserMQ(message, userId);
        }
    }

    /**
     * 推送到用户
     *
     * @param str
     * @param userId
     */
    private void pushMessageToUser(String str, Integer userId, boolean isCollect) {
        pushMessage(str, userId, isCollect);
    }

    /**
     * 推送
     *
     * @param str
     */
    private boolean pushMessage(String str, Integer userId, boolean isCollect) {
        Session session = getSessionByUserId(userId);
        return pushMessage(str, session, isCollect);
    }

    /**
     * 推送
     *
     * @param str
     */
    private boolean pushMessage(String str, Session session, boolean isCollect) {
        if (session == null) {
            return false;
        }
        Integer code = recMessage(str, session, 0);
        if (code == -1) {
            return true;
        } else {
            if (isCollect) {
                //todo collect msg
            }
            return false;
        }
    }

    /**
     * 递归发消息
     *
     * @param str
     * @param session
     * @param count
     * @return
     */
    private Integer recMessage(String str, Session session, Integer count) {
        if (count > 200) {
            return count;
        }
        try {
            Thread.sleep(getSleepTime());
            if (!session.isOpen()) {
                return -2;
            }
            session.getAsyncRemote().sendText(str);
            return -1;
        } catch (Throwable e) {
            if (!"The remote endpoint was in state [TEXT_FULL_WRITING] which is an invalid state for called method"
                    .equalsIgnoreCase(e.getLocalizedMessage())) {
                log.info(e.getLocalizedMessage());
                return -2;
            }
        }
        return recMessage(str, session, ++count);
    }

    private long getSleepTime() {
        return 100 + (long) (Math.random() * 400);
    }

    public void handlingUserMessage(String message, Session session) {
        if (StringUtils.isBlank(message)) {
            return;
        }
        ReceiveMessage receiveMessage = null;
        try {
            receiveMessage = JSONObject.parseObject(message, ReceiveMessage.class);
        } catch (Exception e) {
            log.error("客户端发送不支持的消息类型receiveMessage：" + message);
        }
        if (receiveMessage == null) {
            log.error("客户端发送不支持的消息类型receiveMessage：" + message);
            return;
        }
        String messageType = receiveMessage.getMessageType();
        if (StringUtils.isBlank(messageType)) {
            log.error("客户端发送不支持的消息类型receiveMessage：" + message);
            return;
        }
        try {
            wsMessageHandleFactory.getHandlerByType(messageType)
                    .handlingMessage(getUserIdBySessionId(session.getId()), receiveMessage);
        } catch (Exception e) {
            log.error("客户端发送不支持的消息类型receiveMessage：" + message);
        }

    }

    public void handlingSessionClose(Session session, CloseReason closeReason) {
        sessionClose(session);
        String reason = closeReason != null ? closeReason.getCloseCode().toString() : "";
        log.info("有链接关闭了，" + "关闭原因：" + reason + "，当前连接数量：" + getOnlineNum());
    }

    private void sessionClose(Session session) {
        if (session == null) {
            return;
        }
        String sessionId = session.getId();
        Integer userId = getUserIdBySessionId(sessionId);
        if (userId != null) {
            sessionPool.remove(userId);
        }
        sessionUserIdMap.remove(sessionId);
    }

    public void sendHeartbeatByUserId(Integer userId) {
        Session session = getSessionByUserId(userId);
        sendHeartbeat(session);
    }

    /**
     * 发送心跳
     *
     * @param session
     */
    public void sendHeartbeat(Session session) {
        recMessage("1", session, 0);
    }

}

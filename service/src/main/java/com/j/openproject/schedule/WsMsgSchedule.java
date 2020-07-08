package com.j.openproject.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.j.openproject.websocket.send.WsSend;
import com.j.openproject.websocket.entity.PushMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type WsMsgSchedule
 * @Desc
 * @date 2019年11月21日
 * @Version V1.0
 */
@Slf4j
@Component
public class WsMsgSchedule {

    @Scheduled(cron = "0/4 * * * * ?")
    public void sendMessage() {
        PushMessage message = new PushMessage("1", "hello");
        WsSend.publishMessageToAll(message);

    }
}

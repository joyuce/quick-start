package com.j.openproject.service.imp;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.j.openproject.entity.User;
import com.j.openproject.mapper.UserMapper;
import com.j.openproject.redis.RedisTool;
import com.j.openproject.service.UserService;
import com.j.openproject.thread.FlexThreadPool;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type UserService
 * @Desc
 * @date 2019年11月22日
 * @Version V1.0
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisTool redisTool;

    private AtomicInteger a = new AtomicInteger(0);
    private AtomicInteger rr = new AtomicInteger(0);

    private FlexThreadPool executor = new FlexThreadPool(8, 8, 6L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    String na = a.incrementAndGet() + "";
                    Thread t = new Thread(r, na);
                    log.info("线程：" + na);
                    return t;
                }
            });

    //@AuthCheck(codes = Auth.ONE)
    @Override
    public List<User> getList() {
        String lock = "www";
        for (int i = 0; i < 10000; i++) {

            int j = i;
            //int db = j % 15;
            //String s = j + "";
            //redisTool.delete(s);

            executor.execute(() -> {
                String uuid = UUID.randomUUID().toString();
                boolean rs = redisTool.tryLock(lock, uuid);
                if (rs) {
                    log.info("获取分布式锁成功：" + rr.incrementAndGet());
                    int db = j % 15;
                    String s = j + "";
                    redisTool.setValue(s, s, 10000, db);
                    String v = redisTool.getValue(s, db);
                    String r = "key:" + s + " value:" + v + " db:" + db;
                    if (!s.equals(v)) {
                        log.info(r);
                    }
                }
                redisTool.releaseLock(lock, uuid);
            });
        }
        return userMapper.selectList(null);
    }

    @Override
    public User getTheList() {
        return userMapper.getTheList();
    }
}

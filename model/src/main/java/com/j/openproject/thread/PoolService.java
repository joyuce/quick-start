package com.j.openproject.thread;

import java.util.concurrent.ExecutorService;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type PoolService
 * @Desc
 * @date 2020年02月20日
 * @Version V1.0
 */
@Slf4j
@Component
public class PoolService {

    /**
     * 异步处理线程池
     */
    private FlexThreadPool taskPool = new FlexThreadPool(getPoolMaxSize());


    public ExecutorService getPool(){
        return taskPool;
    }

    /**
     * 当前环境cpu核心数 两倍
     *
     * @return
     */
    private int getPoolMaxSize() {
        int maxSize = Runtime.getRuntime().availableProcessors() * 2;
        log.info("弹性线程池初始化最大值:" + maxSize);
        return maxSize;
    }
}

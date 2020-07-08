package com.j.openproject.controller;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.async.DeferredResult;

import com.j.openproject.annotation.NoLogin;
import com.j.openproject.annotation.RestPathController;
import com.j.openproject.base.CommonPageRs;
import com.j.openproject.base.CommonRs;
import com.j.openproject.entity.User;
import com.j.openproject.redis.RedisTool;
import com.j.openproject.requset.SampleRq;
import com.j.openproject.service.UserService;
import com.j.openproject.web.requset.WeixinRq;
import com.j.openproject.web.response.LoginVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type SampleController
 * @Desc 样例
 * @date 2019年11月21日
 * @Version V1.0
 */
@NoLogin
@Api(value = "测试swagger文档")
@Slf4j
@RestPathController("/sample")
public class SampleController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTool redisTool;

    @ApiOperation("测试异步任务接口")
    @GetMapping("/asy")
    public CommonPageRs agg() throws InterruptedException, ExecutionException {
        //CompletableFuture 默认线程池 ForkJoinPool 线程数固定，
        // 如果是io密集任务，会造成阻塞，需要使用自定义线程池或CachedThreadPool
        //单个异步任务
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000L);
                TimeUnit.SECONDS.sleep(1);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Random random = new Random();
            return random.nextLong();
        }).whenComplete((u, e) -> {
            if (u == null) {
                return;
            }
            if (u instanceof Long) {
                System.err.println("成功" + u);
            }
        });

        //两个异步任务 处理完成后返回
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "1111";
            }
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "2222";
            }
        });
        CompletableFuture<String> result = future1.thenCombine(future2, new BiFunction<String, String, String>() {
            @Override
            public String apply(String t, String u) {
                return t + " " + u;
            }
        });
        System.out.println(result.get());
        return CommonPageRs.createSuccessRs("", 0);
    }

    @PostMapping("/opt")
    public CommonRs<LoginVO> wxLogin(@Valid @RequestBody WeixinRq rq) {
        Optional<LoginVO> loginVO = Optional.ofNullable(new LoginVO("unChange"));

        loginVO.ifPresent((vov) -> {
            rq.setVersion("112212");
            log.error("测试：" + vov.toString());
            vov.setToken("changed");
        });
        log.error(rq.getVersion());
        return CommonRs.createSuccessRs(loginVO.get());
    }

    @Deprecated
    @ApiOperation("测试接口")
    @PostMapping
    public CommonPageRs gg(@Valid @RequestBody SampleRq rq) throws InterruptedException, ExecutionException {
        log.debug("121212");
        log.info(rq.toString());
        Callable<List<User>> callable = () -> {
            List<User> list = userService.getList();
            log.info(list.toString());
            return list;
        };
        FutureTask<List<User>> futureTask = new FutureTask<>(callable);
        Thread t = new Thread(futureTask);
        t.start();
        //futureTask.get();
        //redisTool.s();
        return CommonPageRs.createSuccessRs(futureTask.get(), 0);
    }

    /**
     * 测试异步接口 (可以被全局捕获异常)
     *
     * @return
     */
    @GetMapping("/hello")
    public Callable<String> helloController() {
        log.info(Thread.currentThread().getName() + " 进入helloController方法");
        Callable<String> callable = () -> {
            log.info(Thread.currentThread().getName() + " 进入call方法");
            String say = "hello";
            log.info(Thread.currentThread().getName() + " 从helloService方法返回");
            return say;
        };
        log.info(Thread.currentThread().getName() + " 从helloController方法返回");
        return callable;
    }

    /**
     * 测试异步接口
     *
     * @return
     */
    @GetMapping("/hello1")
    public DeferredResult<String> hello1Controller() {
        log.info(Thread.currentThread().getName() + " 进入helloController方法");
        DeferredResult<String> deferredResult = new DeferredResult<>(3000L);//超时时间3秒
        deferredResult.onTimeout(() -> {
            deferredResult.setResult("超时了");
        });
        taskPool().execute(() -> {
            List<User> list = userService.getList();
            log.info(Thread.currentThread().getName() + " 进入call方法");
            String say = "hello";
            log.info(Thread.currentThread().getName() + " 从helloService方法返回");
            deferredResult.setResult(say);
        });

        log.info(Thread.currentThread().getName() + " 从helloController方法返回");
        return deferredResult;
    }

    @ApiOperation("测试接口")
    @GetMapping("/t")
    public CommonPageRs test(@Valid @RequestBody SampleRq rq) {
        log.error(rq.toString());
        return CommonPageRs.createSuccessRs(userService.getTheList(), 0);
    }

    @ApiOperation("测试redis接口")
    @GetMapping
    public CommonPageRs flushAll() throws InterruptedException {

        redisTool.flushAll();
        return CommonPageRs.createSuccessRs("", 0);
    }

}

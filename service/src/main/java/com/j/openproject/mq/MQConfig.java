package com.j.openproject.mq;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.annotation.Resource;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * 消息队列的配置文件 定义了一些生产者和消费者队列 定义交换机 两者进行绑定
 */
@Slf4j
@Configuration
public class MQConfig implements DisposableBean {

    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Resource(name = "wsBinding")
    private Binding wsBinding;

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    // ----------------- 队列 -----------------------

    /**
     * websocket随机生成队列
     */
    public static String WEBSOCKET_QUEUE = "WEBSOCKET_QUEUE_";

    static {
        try {
            WEBSOCKET_QUEUE = WEBSOCKET_QUEUE + getNetworkAddress();
        } catch (Exception e) {

        }
    }

    public static String getNetworkAddress() {
        String result = null;
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(':') == -1) {
                        String name = ip.getHostAddress();
                        if (name.contains(".")) {
                            result = name.replace(".", "-");
                        }
                    }
                }
            }
            log.info("网卡获取的内网ip：" + result + "原来的内网ip：" + InetAddress.getLocalHost().getHostAddress());
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static String websocketFanoutExchange = "websocketExchange";

    @Value("${spring.rabbitmq.websocket.exchange}")
    public void setWebsocketFanoutExchange(String websocketFanoutExchange) {
        MQConfig.websocketFanoutExchange = websocketFanoutExchange;
    }

    // ----------------- 队列 -----------------------

    @Bean(name = "connectionFactory")
    public ConnectionFactory connectionFactory(@Value("${spring.rabbitmq.host}") String host,
            @Value("${spring.rabbitmq.port}") int port, @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password,
            @Value("${spring.rabbitmq.virtual-host}") String vhost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(host + ":" + port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);

        try (Connection connection = connectionFactory.createConnection();) {
            //Channel channel = connection.createChannel(false);

        } catch (Exception e) {
            log.error("mq declare queue exchange fail ", e);
        }

        return connectionFactory;
    }

    @Bean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    /**
     * websocket相关开始
     */
    @Bean(name = "websocketQueue")
    public Queue websocketQueue() {
        return new Queue(WEBSOCKET_QUEUE);
    }

    @Bean(name = "websocketFanout")
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(websocketFanoutExchange);//配置广播路由器
    }

    @Bean("wsBinding")
    Binding bindingExchange(@Qualifier("websocketQueue") Queue queue,
            @Qualifier("websocketFanout") FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory,
            WebsocketListener websocketListener,
            @Value("${spring.rabbitmq.consumer-prefetch-count}") Integer prefetchCount,
            @Value("${spring.rabbitmq.consumer-concurrent-num}") Integer concurrentConsumers) {
        log.info("开始绑定队列，队列名：" + MQConfig.WEBSOCKET_QUEUE);
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(MQConfig.WEBSOCKET_QUEUE);
        container.setMessageListener(websocketListener);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setPrefetchCount(prefetchCount);
        container.setConcurrentConsumers(concurrentConsumers);
        log.info("结束绑定队列，队列名：" + MQConfig.WEBSOCKET_QUEUE);
        return container;
    }

    /**
     * 系统销毁，删除队列
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        log.info("开始退出");
        rabbitAdmin.deleteQueue(WEBSOCKET_QUEUE);
    }
    /** websocket相关结束 */

}
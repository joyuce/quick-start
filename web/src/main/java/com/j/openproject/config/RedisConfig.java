package com.j.openproject.config;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Joyuce
 * @Type RedisConfig
 * @Desc redis配置
 * @date 2019年11月22日
 * @Version V1.0
 */
@Configuration
public class RedisConfig {

    @Autowired
    private Environment env;

    @Bean
    public JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("redis.pool.maxIdle"))));
        config.setMinIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("redis.pool.minIdle"))));
        config.setMaxTotal(Integer.parseInt(Objects.requireNonNull(env.getProperty("redis.pool.maxActive"))));
        config.setMaxWaitMillis(Long.parseLong(Objects.requireNonNull(env.getProperty("redis.pool.maxWaitMillis"))));
        config.setTestOnBorrow(Boolean.parseBoolean(env.getProperty("redis.pool.testOnBorrow")));
        config.setTestOnReturn(Boolean.parseBoolean(env.getProperty("redis.pool.testOnReturn")));
        return config;
    }

    @Bean
    public JedisPool getJedisPool(JedisPoolConfig jedisPoolConfig) {
        String host = env.getProperty("redis.host");
        int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("redis.port")));
        int connectionTimeout = Integer.parseInt(Objects.requireNonNull(env.getProperty("redis.timeout")));
        String password = env.getProperty("redis.password");
        int database = Integer.parseInt(Objects.requireNonNull(env.getProperty("redis.database.common")));
        JedisPool pool = new JedisPool(jedisPoolConfig, host, port, connectionTimeout, password, database);
        return pool;
    }

}

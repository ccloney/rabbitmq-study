package com.chen.rabbitmqconsumersbasic.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cg
 * @version 1.0
 * @date 2021/7/22 15:14
 *
 *  redisson单机版配置
 */
@Configuration
public class RedissonConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String redisUrl = String.format("redis://%s:%s", redisProperties.getHost() + "", redisProperties.getPort() + "");
        config.useSingleServer().setAddress(redisUrl);
        config.useSingleServer().setDatabase(3);
        return Redisson.create(config);
    }

}

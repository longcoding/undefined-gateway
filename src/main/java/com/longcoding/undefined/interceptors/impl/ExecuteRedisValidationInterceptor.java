package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.configs.ServiceConfig;
import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.RedisValidator;
import com.longcoding.undefined.helpers.JedisFactory;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.interceptors.RedisBaseValidationInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Created by longcoding on 16. 4. 7..
 * Updated by longcoding on 18. 12. 26..
 */

@Slf4j
@EnableConfigurationProperties(ServiceConfig.class)
public class ExecuteRedisValidationInterceptor<T> extends AbstractBaseInterceptor {

    @Autowired
    ApplicationContext context;

    @Autowired
    ServiceConfig serviceConfig;

    @Autowired
    JedisFactory jedisFactory;

    private static ExecutorService executor;


    @PostConstruct
    private void initializeInterceptor() {
        //executor = Executors.newFixedThreadPool(serviceConfig.async.threadCount);

    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RedisValidator redisValidator = (RedisValidator) request.getAttribute(Const.OBJECT_GET_REDIS_VALIDATION);

        try {
            redisValidator.getJedisMulti().exec();
            redisValidator.getJedisMulti().close();
        } catch (JedisConnectionException e) {
            log.error("{}", e);
            generateException(ExceptionType.E_1101_API_GATEWAY_IS_EXHAUSTED);
        } finally {
            redisValidator.getJedis().close();
        }

        LinkedHashMap<String, T> futureMethodQueue = redisValidator.getFutureMethodQueue();

        Jedis jedis = jedisFactory.getInstance();
        Transaction jedisMulti = jedis.multi();

        T futureValue;
        boolean interceptorResult = false;
        for (String className : futureMethodQueue.keySet()) {
            futureValue = (futureMethodQueue.get(className));
            try {
                RedisBaseValidationInterceptor objectBean = (RedisBaseValidationInterceptor) context.getBean(className);
                interceptorResult = objectBean.executeJudge(futureValue, jedisMulti);
            } catch (JedisDataException e) {
                // This is Jedis Bug. I wish it will be fixed.
                generateException(ExceptionType.E_1101_API_GATEWAY_IS_EXHAUSTED);
            } catch (NullPointerException e) {
                // AppKey is not exist or service is exhausted.
                generateException(ExceptionType.E_1101_API_GATEWAY_IS_EXHAUSTED);
            }
            if (!interceptorResult){
                generateException(ExceptionType.E_1009_SERVICE_RATELIMIT_OVER);
                jedisMulti.exec();
                jedis.close();
                return false;
            }
        }
        jedisMulti.exec();
        jedis.close();

        return true;

    }
}

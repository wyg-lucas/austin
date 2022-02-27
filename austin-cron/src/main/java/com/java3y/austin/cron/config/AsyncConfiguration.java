package com.java3y.austin.cron.config;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 处理定时任务的线程池配置信息，为@Async注解服务
 * 自定义线程池配置
 *
 * @author 3y
 * @see TaskExecutionAutoConfiguration
 */
@Slf4j
@Configuration
@EnableAsync
@EnableConfigurationProperties(AsyncExecutionProperties.class)
public class AsyncConfiguration implements AsyncConfigurer {
    @Bean("austinExecutor")
    @Primary
    public ThreadPoolTaskExecutor executor(AsyncExecutionProperties properties) {
        log.info("funExecutor -- init ");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCoreSize()); // 核心线程数
        executor.setMaxPoolSize(properties.getMaxSize()); // 最大线程数
        executor.setKeepAliveSeconds(properties.getKeepAlive()); // 最大存活时间
        executor.setQueueCapacity(properties.getQueueCapacity()); // 阻塞队列容量
        executor.setThreadNamePrefix(properties.getThreadNamePrefix()); // 设置名称前缀
        executor.setRejectedExecutionHandler(properties.getRejectedHandler().getHandler());// 设置拒绝策略
        executor.setAllowCoreThreadTimeOut(properties.isAllowCoreThreadTimeout());// 是否允许核心线程超时
        executor.setWaitForTasksToCompleteOnShutdown(properties.isWaitForTasksToCompleteOnShutDown());
        executor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());
        log.info("austinExecutor: {} ", executor);
        executor.initialize();
        return executor;
    }


    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> log.error("austinExecutor execute fail!method:{},params:{},ex:{}", method, params, Throwables.getStackTraceAsString(ex));
    }
}

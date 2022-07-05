package com.vote.common.evenntbus.config;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vote.common.evenntbus.ThreadPoolTaskExecutorProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Executor;

@Slf4j
@EnableConfigurationProperties(ThreadPoolTaskExecutorProperties.class)
@Configuration
public class EventBusConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ThreadPoolTaskExecutorProperties poolTaskExecutorProperties;

    @Bean("eventExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolTaskExecutorProperties.getCorePoolSize());
        executor.setMaxPoolSize(poolTaskExecutorProperties.getMaxPoolSize());
        executor.setQueueCapacity(poolTaskExecutorProperties.getQueueCapacity());
        executor.setThreadNamePrefix(poolTaskExecutorProperties.getThreadNamePrefix());
        executor.setAllowCoreThreadTimeOut(poolTaskExecutorProperties.isAllowCoreThreadTimeOut());
        executor.setKeepAliveSeconds(poolTaskExecutorProperties.getKeepAliveSeconds());
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        initEventBusMethodRepository(event.getApplicationContext());

    }

    class MdcTaskDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run();
            };
        }
    }

    @Bean
    public EventBus eventBus() {
        return new AsyncEventBus(getAsyncExecutor());
    }

    private void initEventBusMethodRepository(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            return;
        }
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        EventBus eventBus = applicationContext.getBean(EventBus.class);

        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            Map<Method, Subscribe> annotatedMethods = null;
            try {
                annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                        (MethodIntrospector.MetadataLookup<Subscribe>) method -> AnnotatedElementUtils.findMergedAnnotation(method, Subscribe.class));
            } catch (Throwable ex) {
                log.error("EventBus method-Subscribe resolve error for bean[" + beanDefinitionName + "].", ex);
            }
            if (annotatedMethods==null || annotatedMethods.isEmpty()) {
                continue;
            }

            if (annotatedMethods.size() > 1) {
                log.error("Match more than 1 subscribe method, expected 1 method.");
                continue;
            }

            log.info("EventBus method-Subscribe register bean[{}]", beanDefinitionName);
            eventBus.register(bean);
        }
    }
}

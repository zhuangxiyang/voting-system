package com.vote.common.threadpool;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 多线程线程池
 */
public class ThreadPoolService {
    public static final int DEFAULT_CORE_SIZE=100;
    private static Executor executor;
    public static synchronized Executor getInstance() {
        if (executor == null){
            executor = TtlExecutors.getTtlExecutor(Executors.newFixedThreadPool(DEFAULT_CORE_SIZE));
        }
        return executor;
    }
    private ThreadPoolService() {}
}


package com.alibaba.ttl.threadpool;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.agent.TtlAgent;

import javax.annotation.Nullable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Factory Utils for getting TTL wrapper of jdk executors.
 * <p>
 * <b><i>Note:</i></b>
 * <ul>
 * <li>all method is {@code null}-safe, when input {@code executor} parameter is {@code null}, return {@code null}.</li>
 * <li>skip decorating thread pool/{@code executor}(aka. just return input {@code executor})
 * when ttl agent is loaded, Or when input {@code executor} is already decorated.</li>
 * </ul>
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see java.util.concurrent.Executor
 * @see java.util.concurrent.ExecutorService
 * @see java.util.concurrent.ThreadPoolExecutor
 * @see java.util.concurrent.ScheduledThreadPoolExecutor
 * @see java.util.concurrent.Executors
 * @see java.util.concurrent.CompletionService
 * @see java.util.concurrent.ExecutorCompletionService
 * @since 0.9.0
 */
public final class TtlExecutors {
    /**
     * {@link TransmittableThreadLocal} Wrapper of {@link Executor},
     * transmit the {@link TransmittableThreadLocal} from the task submit time of {@link Runnable}
     * to the execution time of {@link Runnable}.
     */
    @Nullable
    public static Executor getTtlExecutor(@Nullable Executor executor) {
        if (TtlAgent.isTtlAgentLoaded() || null == executor || executor instanceof ExecutorTtlWrapper) {
            return executor;
        }
        return new ExecutorTtlWrapper(executor);
    }

    /**
     * {@link TransmittableThreadLocal} Wrapper of {@link ExecutorService},
     * transmit the {@link TransmittableThreadLocal} from the task submit time of {@link Runnable} or {@link java.util.concurrent.Callable}
     * to the execution time of {@link Runnable} or {@link java.util.concurrent.Callable}.
     */
    @Nullable
    public static ExecutorService getTtlExecutorService(@Nullable ExecutorService executorService) {
        if (TtlAgent.isTtlAgentLoaded() || executorService == null || executorService instanceof ExecutorServiceTtlWrapper) {
            return executorService;
        }
        return new ExecutorServiceTtlWrapper(executorService);
    }

    /**
     * {@link TransmittableThreadLocal} Wrapper of {@link ScheduledExecutorService},
     * transmit the {@link TransmittableThreadLocal} from the task submit time of {@link Runnable} or {@link java.util.concurrent.Callable}
     * to the execution time of {@link Runnable} or {@link java.util.concurrent.Callable}.
     */
    @Nullable
    public static ScheduledExecutorService getTtlScheduledExecutorService(@Nullable ScheduledExecutorService scheduledExecutorService) {
        if (TtlAgent.isTtlAgentLoaded() || scheduledExecutorService == null || scheduledExecutorService instanceof ScheduledExecutorServiceTtlWrapper) {
            return scheduledExecutorService;
        }
        return new ScheduledExecutorServiceTtlWrapper(scheduledExecutorService);
    }

    /**
     * check the executor is TTL wrapper executor or not.
     * <p>
     * if the parameter executor is TTL wrapper, return {@code true}, otherwise {@code false}.
     * <p>
     * NOTE: if input executor is {@code null}, return {@code false}.
     *
     * @param executor input executor
     * @param <T>      Executor type
     * @see #getTtlExecutor(Executor)
     * @see #getTtlExecutorService(ExecutorService)
     * @see #getTtlScheduledExecutorService(ScheduledExecutorService)
     * @see #unwrap(Executor)
     * @since 2.8.0
     */
    public static <T extends Executor> boolean isTtlWrapper(@Nullable T executor) {
        return (executor instanceof ExecutorTtlWrapper);
    }

    /**
     * Unwrap TTL wrapper executor to the original/underneath one.
     * <p>
     * if the parameter executor is TTL wrapper, return the original/underneath executor;
     * otherwise, just return the input parameter executor.
     * <p>
     * NOTE: if input executor is {@code null}, return {@code null}.
     *
     * @param executor input executor
     * @param <T>      Executor type
     * @see #getTtlExecutor(Executor)
     * @see #getTtlExecutorService(ExecutorService)
     * @see #getTtlScheduledExecutorService(ScheduledExecutorService)
     * @see #isTtlWrapper(Executor)
     * @since 2.8.0
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Executor> T unwrap(@Nullable T executor) {
        if (!isTtlWrapper(executor)) return executor;

        return (T) ((ExecutorTtlWrapper) executor).unwrap();
    }

    private TtlExecutors() {
    }
}
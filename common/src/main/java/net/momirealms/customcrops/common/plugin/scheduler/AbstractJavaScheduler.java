/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.momirealms.customcrops.common.plugin.scheduler;

import net.momirealms.customcrops.common.plugin.CustomCropsPlugin;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Abstract implementation of {@link SchedulerAdapter} using a {@link ScheduledExecutorService}.
 */
public abstract class AbstractJavaScheduler<T, W> implements SchedulerAdapter<T, W> {
    private static final int PARALLELISM = 16;

    private final CustomCropsPlugin plugin;

    private final ScheduledThreadPoolExecutor scheduler;
    private final ForkJoinPool worker;

    public AbstractJavaScheduler(CustomCropsPlugin plugin) {
        this.plugin = plugin;

        this.scheduler = new ScheduledThreadPoolExecutor(4, r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName("customcrops-scheduler");
            return thread;
        });
        this.scheduler.setRemoveOnCancelPolicy(true);
        this.scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.worker = new ForkJoinPool(PARALLELISM, new WorkerThreadFactory(), new ExceptionHandler(), false);
    }

    @Override
    public Executor async() {
        return this.worker;
    }

    @Override
    public SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.schedule(() -> this.worker.execute(task), delay, unit);
        return new JavaCancellable(future);
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable task, long delay, long interval, TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(() -> this.worker.execute(task), delay, interval, unit);
        return new JavaCancellable(future);
    }

    @Override
    public void shutdownScheduler() {
        this.scheduler.shutdown();
        try {
            if (!this.scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                this.plugin.getPluginLogger().severe("Timed out waiting for the CustomCrops scheduler to terminate");
                reportRunningTasks(thread -> thread.getName().equals("customcrops-scheduler"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdownExecutor() {
        this.worker.shutdown();
        try {
            if (!this.worker.awaitTermination(1, TimeUnit.MINUTES)) {
                this.plugin.getPluginLogger().severe("Timed out waiting for the CustomCrops worker thread pool to terminate");
                reportRunningTasks(thread -> thread.getName().startsWith("customcrops-worker-"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void reportRunningTasks(Predicate<Thread> predicate) {
        Thread.getAllStackTraces().forEach((thread, stack) -> {
            if (predicate.test(thread)) {
                this.plugin.getPluginLogger().warn("Thread " + thread.getName() + " is blocked, and may be the reason for the slow shutdown!\n" +
                        Arrays.stream(stack).map(el -> "  " + el).collect(Collectors.joining("\n"))
                );
            }
        });
    }

    private static final class WorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        private static final AtomicInteger COUNT = new AtomicInteger(0);

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            thread.setDaemon(true);
            thread.setName("customcrops-worker-" + COUNT.getAndIncrement());
            return thread;
        }
    }

    private final class ExceptionHandler implements UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            AbstractJavaScheduler.this.plugin.getPluginLogger().warn("Thread " + t.getName() + " threw an uncaught exception", e);
        }
    }

    public static class JavaCancellable implements SchedulerTask {

        private final ScheduledFuture<?> future;

        public JavaCancellable(ScheduledFuture<?> future) {
            this.future = future;
        }

        @Override
        public void cancel() {
            this.future.cancel(false);
        }

        @Override
        public boolean isCancelled() {
            return future.isCancelled();
        }
    }
}

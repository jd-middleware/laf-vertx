package com.jd.laf.web.vertx.service;

import com.jd.laf.web.vertx.Environment;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息处理
 */
public abstract class MessageDaemon<T> implements Daemon {

    protected final Logger logger;

    //队列大小
    protected int queueSize = 1000;
    //从队列获取数据超时时间
    protected long pollTimeout = 5000;

    protected String queueSizeKey;
    protected String pollTimeoutKey;
    protected String threadName = this.getClass().getSimpleName();
    protected String daemonName = this.getClass().getSimpleName();

    protected BlockingQueue<T> events;
    protected Thread thread;
    protected AtomicBoolean started = new AtomicBoolean(false);

    public MessageDaemon() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public synchronized void start(final Environment context) {
        if (started.compareAndSet(false, true)) {
            doStart(context);
            logger.info(daemonName + " is started!");
        }
    }

    @Override
    public synchronized void stop() {
        if (started.compareAndSet(true, false)) {
            doStop();
            logger.info(daemonName + " is stopped.");
        }
    }

    /**
     * 启动
     *
     * @param context
     */
    protected void doStart(final Environment context) {
        queueSize = context.getPositive(queueSizeKey, 1000);
        pollTimeout = context.getPositive(pollTimeoutKey, 5000);
        events = new LinkedBlockingQueue<>(queueSize);
        thread = new Thread(new TaskConsumer(events, pollTimeout), threadName);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 停止
     */
    protected void doStop() {
        if (events != null) {
            //通知一下，触发阻塞的线程
            T message = stopMessage();
            if (message != null) {
                events.add(message);
            }
        }
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    /**
     * 添加消息
     *
     * @param message
     */
    public void addMessage(final T message) {
        if (message == null) {
            return;
        }
        try {
            //如果没有添加成功则丢弃
            events.add(message);
        } catch (IllegalStateException e) {
        }
    }

    /**
     * 添加消息
     *
     * @param message
     */
    public void putMessage(final T message) {
        if (message == null) {
            return;
        }
        try {
            events.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isStarted() {
        return started.get();
    }

    /**
     * 创建退出的消息
     *
     * @return
     */
    protected T stopMessage() {
        return null;
    }

    /**
     * 收到消息
     *
     * @param message
     */
    protected abstract void onMessage(T message);

    /**
     * 没有消息
     */
    protected void onEmpty() {

    }

    /**
     * 队列消费者
     */
    protected class TaskConsumer implements Runnable {

        protected BlockingQueue<T> events;
        protected long pollTimeout;

        public TaskConsumer(BlockingQueue<T> events, long pollTimeout) {
            this.events = events;
            this.pollTimeout = pollTimeout;
        }

        @Override
        public void run() {
            while (isStarted()) {
                try {
                    //拿到更新的消息
                    final T message = events.poll(pollTimeout, TimeUnit.MICROSECONDS);
                    if (!isStarted()) {
                        return;
                    } else if (message != null) {
                        onMessage(message);
                    } else {
                        onEmpty();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}

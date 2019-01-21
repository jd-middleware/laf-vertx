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

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    //队列大小
    protected int queueSize = 1000;
    //从队列获取数据超时时间
    protected long pollTimeout = 5000;
    //检查周期
    protected long checkInterval;

    protected String queueSizeKey = "message.queue.size";
    protected String pollTimeoutKey = "message.poll.timeout";
    protected String daemonKey;
    protected String threadName = this.getClass().getSimpleName();
    protected String daemonName = this.getClass().getSimpleName();

    protected BlockingQueue<T> events;
    protected Thread thread;
    protected AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public synchronized void start(final Environment context) throws Exception {
        if (started.compareAndSet(false, true)) {
            queueSize = context.getPositive(queueSizeKey, queueSize);
            pollTimeout = context.getPositive(pollTimeoutKey, pollTimeout);
            events = new LinkedBlockingQueue<>(queueSize);
            doStart(context);
            thread = new Thread(new TaskConsumer(events, pollTimeout, checkInterval), threadName);
            thread.setDaemon(true);
            thread.start();
            if (daemonKey != null && !daemonKey.isEmpty()) {
                context.put(daemonKey, this);
            }

            logger.info(daemonName + " is started!");
        }
    }

    @Override
    public synchronized void stop() {
        if (started.compareAndSet(true, false)) {
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
            doStop();
            logger.info(daemonName + " is stopped.");
        }
    }

    /**
     * 启动
     *
     * @param context
     */
    protected void doStart(final Environment context) throws Exception {
    }

    /**
     * 停止
     */
    protected void doStop() {
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
            logger.error(e);
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
     * 定期检查
     */
    protected void onCheck() {

    }

    /**
     * 队列消费者
     */
    protected class TaskConsumer implements Runnable {

        protected BlockingQueue<T> events;
        protected long pollTimeout;
        protected long checkInterval;

        public TaskConsumer(BlockingQueue<T> events, long pollTimeout, long checkInterval) {
            this.events = events;
            this.pollTimeout = pollTimeout;
            this.checkInterval = checkInterval;
        }

        @Override
        public void run() {
            long last = System.currentTimeMillis();
            long now;
            T message;
            while (isStarted()) {
                try {
                    //拿到更新的消息
                    message = events.poll(pollTimeout, TimeUnit.MICROSECONDS);
                    if (!isStarted()) {
                        return;
                    } else if (message != null) {
                        onMessage(message);
                    } else {
                        onEmpty();
                    }
                    //超过了全量检查周期
                    if (checkInterval > 0) {
                        now = System.currentTimeMillis();
                        if (now - last >= checkInterval) {
                            last = now;
                            //全量检查
                            onCheck();
                            //再次设置一下时间，避免检查时间过长
                            last = System.currentTimeMillis();
                        }
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

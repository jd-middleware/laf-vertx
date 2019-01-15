package com.jd.laf.web.vertx.spring.boot.cluster;

import io.vertx.core.json.JsonObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;


@ConfigurationProperties("vertx.cluster-manager.zookeeper")
public class ZookeeperProperties {

    private String node;
    private List<String> hosts;
    private int sessionTimeout = 20000;
    private int connectTimeout = 3000;
    private String rootPath = "laf.web";
    private RetryPolicy retry = new RetryPolicy();

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public RetryPolicy getRetry() {
        return retry;
    }

    public void setRetry(RetryPolicy retry) {
        this.retry = retry;
    }

    /**
     * 重试策略
     */
    public static class RetryPolicy {
        private int initialSleepTime = 100;
        private int interval = 10000;
        private int maxTimes = 5;

        public int getInitialSleepTime() {
            return initialSleepTime;
        }

        public void setInitialSleepTime(int initialSleepTime) {
            this.initialSleepTime = initialSleepTime;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public int getMaxTimes() {
            return maxTimes;
        }

        public void setMaxTimes(int maxTimes) {
            this.maxTimes = maxTimes;
        }

        JsonObject toJson() {
            return new JsonObject()
                    .put("initialSleepTime", initialSleepTime)
                    .put("intervalTimes", interval)
                    .put("maxTimes", maxTimes);
        }
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("zookeeperHosts", StringUtils.collectionToCommaDelimitedString(hosts))
                .put("sessionTimeout", sessionTimeout)
                .put("connectTimeout", connectTimeout)
                .put("rootPath", rootPath)
                .put("retry", retry.toJson());
    }

}

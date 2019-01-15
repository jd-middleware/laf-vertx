package com.jd.laf.ignite.spring.boot;

import org.apache.ignite.configuration.DataPageEvictionMode;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;

import static org.apache.ignite.configuration.DataRegionConfiguration.*;
import static org.apache.ignite.configuration.DataStorageConfiguration.DFLT_DATA_REG_DEFAULT_NAME;

public class DataRegionProperties {

    protected String name = DFLT_DATA_REG_DEFAULT_NAME;
    protected long maxSize = DataStorageConfiguration.DFLT_DATA_REGION_MAX_SIZE;
    protected long initialSize = Math.min(
            DataStorageConfiguration.DFLT_DATA_REGION_MAX_SIZE, DataStorageConfiguration.DFLT_DATA_REGION_INITIAL_SIZE);
    protected String swapPath;
    protected DataPageEvictionMode pageEvictionMode = DataPageEvictionMode.DISABLED;
    protected double evictionThreshold = 0.9;
    protected int emptyPagesPoolSize = 100;
    protected boolean metricsEnabled = DFLT_METRICS_ENABLED;
    protected int metricsSubIntervalCount = DFLT_SUB_INTERVALS;
    protected long metricsRateTimeInterval = DFLT_RATE_TIME_INTERVAL_MILLIS;
    protected boolean persistenceEnabled = false;
    protected long checkpointPageBufferSize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public long getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(long initialSize) {
        this.initialSize = initialSize;
    }

    public String getSwapPath() {
        return swapPath;
    }

    public void setSwapPath(String swapPath) {
        this.swapPath = swapPath;
    }

    public DataPageEvictionMode getPageEvictionMode() {
        return pageEvictionMode;
    }

    public void setPageEvictionMode(DataPageEvictionMode pageEvictionMode) {
        this.pageEvictionMode = pageEvictionMode;
    }

    public double getEvictionThreshold() {
        return evictionThreshold;
    }

    public void setEvictionThreshold(double evictionThreshold) {
        this.evictionThreshold = evictionThreshold;
    }

    public int getEmptyPagesPoolSize() {
        return emptyPagesPoolSize;
    }

    public void setEmptyPagesPoolSize(int emptyPagesPoolSize) {
        this.emptyPagesPoolSize = emptyPagesPoolSize;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public void setMetricsEnabled(boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
    }

    public int getMetricsSubIntervalCount() {
        return metricsSubIntervalCount;
    }

    public void setMetricsSubIntervalCount(int metricsSubIntervalCount) {
        this.metricsSubIntervalCount = metricsSubIntervalCount;
    }

    public long getMetricsRateTimeInterval() {
        return metricsRateTimeInterval;
    }

    public void setMetricsRateTimeInterval(long metricsRateTimeInterval) {
        this.metricsRateTimeInterval = metricsRateTimeInterval;
    }

    public boolean isPersistenceEnabled() {
        return persistenceEnabled;
    }

    public void setPersistenceEnabled(boolean persistenceEnabled) {
        this.persistenceEnabled = persistenceEnabled;
    }

    public long getCheckpointPageBufferSize() {
        return checkpointPageBufferSize;
    }

    public void setCheckpointPageBufferSize(long checkpointPageBufferSize) {
        this.checkpointPageBufferSize = checkpointPageBufferSize;
    }

    public DataRegionConfiguration build() {
        DataRegionConfiguration result = new DataRegionConfiguration();
        result.setCheckpointPageBufferSize(checkpointPageBufferSize);
        result.setEmptyPagesPoolSize(emptyPagesPoolSize);
        result.setEvictionThreshold(evictionThreshold);
        result.setInitialSize(initialSize);
        result.setMaxSize(maxSize);
        result.setMetricsEnabled(metricsEnabled);
        result.setMetricsRateTimeInterval(metricsRateTimeInterval);
        result.setMetricsSubIntervalCount(metricsSubIntervalCount);
        result.setName(name);
        result.setPersistenceEnabled(persistenceEnabled);
        result.setSwapPath(swapPath);
        return result;
    }
}

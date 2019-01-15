package com.jd.laf.ignite.spring.boot;

import org.apache.ignite.configuration.CheckpointWriteOrder;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.WALMode;
import org.apache.ignite.internal.processors.cache.persistence.file.AsyncFileIOFactory;
import org.apache.ignite.internal.processors.cache.persistence.file.RandomAccessFileIOFactory;

import java.util.ArrayList;
import java.util.List;

import static org.apache.ignite.configuration.DataStorageConfiguration.*;

public class DataStorageProperties {

    /**
     * Default initial size of a memory chunk for the system cache (40 MB).
     */
    public static final long DFLT_SYS_CACHE_INIT_SIZE = 40 * 1024 * 1024;

    /**
     * Default max size of a memory chunk for the system cache (100 MB).
     */
    public static final long DFLT_SYS_CACHE_MAX_SIZE = 100 * 1024 * 1024;

    protected long systemRegionInitialSize = DFLT_SYS_CACHE_INIT_SIZE;
    protected long systemRegionMaxSize = DFLT_SYS_CACHE_MAX_SIZE;
    protected int pageSize = 1024 * 8;
    protected int concurrencyLevel;
    protected List<DataRegionProperties> regions;
    protected String storagePath;
    protected long checkpointFrequency = DFLT_CHECKPOINT_FREQ;
    protected long lockWaitTime = DFLT_LOCK_WAIT_TIME;
    protected int checkpointThreads = DFLT_CHECKPOINT_THREADS;
    protected CheckpointWriteOrder checkpointWriteOrder = DFLT_CHECKPOINT_WRITE_ORDER;
    protected int walHistorySize = DFLT_WAL_HISTORY_SIZE;
    protected int walSegments = DFLT_WAL_SEGMENTS;
    protected int walSegmentSize = DFLT_WAL_SEGMENT_SIZE;
    protected String walPath = DFLT_WAL_PATH;
    protected String walArchivePath = DFLT_WAL_ARCHIVE_PATH;
    protected long walAutoArchiveAfterInactivity = -1;
    protected boolean walCompactionEnabled = DFLT_WAL_COMPACTION_ENABLED;
    protected WALMode walMode = DFLT_WAL_MODE;
    protected int walThreadLocalBufferSize = DFLT_TLB_SIZE;
    protected int walBufferSize;
    protected long walFlushFrequency = DFLT_WAL_FLUSH_FREQ;
    protected long walFsyncDelay = DFLT_WAL_FSYNC_DELAY;
    protected int walRecordIteratorBufferSize = DFLT_WAL_RECORD_ITERATOR_BUFFER_SIZE;
    protected boolean alwaysWriteFullPages = DFLT_WAL_ALWAYS_WRITE_FULL_PAGES;
    protected boolean asyncFileIoFactory = true;
    protected boolean metricsEnabled = DFLT_METRICS_ENABLED;
    protected int metricsSubIntervalCnt = DFLT_SUB_INTERVALS;
    protected long metricsRateTimeInterval = DFLT_RATE_TIME_INTERVAL_MILLIS;
    protected boolean writeThrottlingEnabled = DFLT_WRITE_THROTTLING_ENABLED;

    public long getSystemRegionInitialSize() {
        return systemRegionInitialSize;
    }

    public void setSystemRegionInitialSize(long systemRegionInitialSize) {
        this.systemRegionInitialSize = systemRegionInitialSize;
    }

    public long getSystemRegionMaxSize() {
        return systemRegionMaxSize;
    }

    public void setSystemRegionMaxSize(long systemRegionMaxSize) {
        this.systemRegionMaxSize = systemRegionMaxSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public void setConcurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
    }

    public List<DataRegionProperties> getRegions() {
        return regions;
    }

    public void setRegions(List<DataRegionProperties> regions) {
        this.regions = regions;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public long getCheckpointFrequency() {
        return checkpointFrequency;
    }

    public void setCheckpointFrequency(long checkpointFrequency) {
        this.checkpointFrequency = checkpointFrequency;
    }

    public long getLockWaitTime() {
        return lockWaitTime;
    }

    public void setLockWaitTime(long lockWaitTime) {
        this.lockWaitTime = lockWaitTime;
    }

    public int getCheckpointThreads() {
        return checkpointThreads;
    }

    public void setCheckpointThreads(int checkpointThreads) {
        this.checkpointThreads = checkpointThreads;
    }

    public CheckpointWriteOrder getCheckpointWriteOrder() {
        return checkpointWriteOrder;
    }

    public void setCheckpointWriteOrder(CheckpointWriteOrder checkpointWriteOrder) {
        this.checkpointWriteOrder = checkpointWriteOrder;
    }

    public int getWalHistorySize() {
        return walHistorySize;
    }

    public void setWalHistorySize(int walHistorySize) {
        this.walHistorySize = walHistorySize;
    }

    public int getWalSegments() {
        return walSegments;
    }

    public void setWalSegments(int walSegments) {
        this.walSegments = walSegments;
    }

    public int getWalSegmentSize() {
        return walSegmentSize;
    }

    public void setWalSegmentSize(int walSegmentSize) {
        this.walSegmentSize = walSegmentSize;
    }

    public String getWalPath() {
        return walPath;
    }

    public void setWalPath(String walPath) {
        this.walPath = walPath;
    }

    public String getWalArchivePath() {
        return walArchivePath;
    }

    public void setWalArchivePath(String walArchivePath) {
        this.walArchivePath = walArchivePath;
    }

    public long getWalAutoArchiveAfterInactivity() {
        return walAutoArchiveAfterInactivity;
    }

    public void setWalAutoArchiveAfterInactivity(long walAutoArchiveAfterInactivity) {
        this.walAutoArchiveAfterInactivity = walAutoArchiveAfterInactivity;
    }

    public boolean isWalCompactionEnabled() {
        return walCompactionEnabled;
    }

    public void setWalCompactionEnabled(boolean walCompactionEnabled) {
        this.walCompactionEnabled = walCompactionEnabled;
    }

    public WALMode getWalMode() {
        return walMode;
    }

    public void setWalMode(WALMode walMode) {
        this.walMode = walMode;
    }

    public int getWalThreadLocalBufferSize() {
        return walThreadLocalBufferSize;
    }

    public void setWalThreadLocalBufferSize(int walThreadLocalBufferSize) {
        this.walThreadLocalBufferSize = walThreadLocalBufferSize;
    }

    public int getWalBufferSize() {
        return walBufferSize;
    }

    public void setWalBufferSize(int walBufferSize) {
        this.walBufferSize = walBufferSize;
    }

    public long getWalFlushFrequency() {
        return walFlushFrequency;
    }

    public void setWalFlushFrequency(long walFlushFrequency) {
        this.walFlushFrequency = walFlushFrequency;
    }

    public long getWalFsyncDelay() {
        return walFsyncDelay;
    }

    public void setWalFsyncDelay(long walFsyncDelay) {
        this.walFsyncDelay = walFsyncDelay;
    }

    public int getWalRecordIteratorBufferSize() {
        return walRecordIteratorBufferSize;
    }

    public void setWalRecordIteratorBufferSize(int walRecordIteratorBufferSize) {
        this.walRecordIteratorBufferSize = walRecordIteratorBufferSize;
    }

    public boolean isAlwaysWriteFullPages() {
        return alwaysWriteFullPages;
    }

    public void setAlwaysWriteFullPages(boolean alwaysWriteFullPages) {
        this.alwaysWriteFullPages = alwaysWriteFullPages;
    }

    public boolean isAsyncFileIoFactory() {
        return asyncFileIoFactory;
    }

    public void setAsyncFileIoFactory(boolean asyncFileIoFactory) {
        this.asyncFileIoFactory = asyncFileIoFactory;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public void setMetricsEnabled(boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
    }

    public int getMetricsSubIntervalCnt() {
        return metricsSubIntervalCnt;
    }

    public void setMetricsSubIntervalCnt(int metricsSubIntervalCnt) {
        this.metricsSubIntervalCnt = metricsSubIntervalCnt;
    }

    public long getMetricsRateTimeInterval() {
        return metricsRateTimeInterval;
    }

    public void setMetricsRateTimeInterval(long metricsRateTimeInterval) {
        this.metricsRateTimeInterval = metricsRateTimeInterval;
    }

    public boolean isWriteThrottlingEnabled() {
        return writeThrottlingEnabled;
    }

    public void setWriteThrottlingEnabled(boolean writeThrottlingEnabled) {
        this.writeThrottlingEnabled = writeThrottlingEnabled;
    }

    public DataStorageConfiguration build() {
        DataStorageConfiguration result = new DataStorageConfiguration();
        result.setSystemRegionInitialSize(systemRegionInitialSize);
        result.setSystemRegionMaxSize(systemRegionMaxSize);
        result.setPageSize(pageSize);
        result.setConcurrencyLevel(concurrencyLevel);
        result.setStoragePath(storagePath);
        result.setLockWaitTime(lockWaitTime);
        result.setCheckpointFrequency(checkpointFrequency);
        result.setCheckpointThreads(checkpointThreads);
        result.setCheckpointWriteOrder(checkpointWriteOrder);
        result.setWalHistorySize(walHistorySize);
        result.setWalSegments(walSegments);
        result.setWalSegmentSize(walSegmentSize);
        result.setWalPath(walPath);
        result.setWalArchivePath(walArchivePath);
        result.setWalAutoArchiveAfterInactivity(walAutoArchiveAfterInactivity);
        result.setWalCompactionEnabled(walCompactionEnabled);
        result.setWalMode(walMode);
        result.setWalBufferSize(walBufferSize);
        result.setWalThreadLocalBufferSize(walThreadLocalBufferSize);
        result.setWalFlushFrequency(walFlushFrequency);
        result.setWalFsyncDelayNanos(walFsyncDelay);
        result.setWalRecordIteratorBufferSize(walRecordIteratorBufferSize);
        result.setAlwaysWriteFullPages(alwaysWriteFullPages);
        result.setMetricsEnabled(metricsEnabled);
        result.setMetricsSubIntervalCount(metricsSubIntervalCnt);
        result.setMetricsRateTimeInterval(metricsRateTimeInterval);
        result.setWriteThrottlingEnabled(writeThrottlingEnabled);
        result.setFileIOFactory(asyncFileIoFactory ? new AsyncFileIOFactory() : new RandomAccessFileIOFactory());
        if (regions != null) {
            List<DataRegionConfiguration> configurations = new ArrayList<>(regions.size());
            for (DataRegionProperties region : regions) {
                //默认数据分区
                if (DFLT_DATA_REG_DEFAULT_NAME.equals(region.getName())) {
                    result.setDefaultDataRegionConfiguration(region.build());
                } else {
                    configurations.add(region.build());
                }
            }
            result.setDataRegionConfigurations(configurations.toArray(new DataRegionConfiguration[configurations.size()]));
        }
        return result;
    }
}

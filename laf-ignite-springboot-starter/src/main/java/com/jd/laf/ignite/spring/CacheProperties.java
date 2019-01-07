package com.jd.laf.ignite.spring;

import org.apache.ignite.cache.*;
import org.apache.ignite.configuration.CacheConfiguration;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.ignite.cache.CacheWriteSynchronizationMode.PRIMARY_SYNC;
import static org.apache.ignite.configuration.CacheConfiguration.*;

public class CacheProperties {
    //名称配置成数组
    protected String[] name;
    protected String groupName;
    protected String dataRegionName;
    protected boolean onheapCacheEnabled;
    protected boolean eagerTtl = DFLT_EAGER_TTL;
    protected long defaultLockTimeout = DFLT_LOCK_TIMEOUT;
    protected boolean loadPreviousValue = DFLT_LOAD_PREV_VAL;
    protected CacheMode cacheMode = DFLT_CACHE_MODE;
    protected CacheAtomicityMode atomicityMode = DFLT_CACHE_ATOMICITY_MODE;
    protected int backups = DFLT_BACKUPS;
    protected boolean invalidate = DFLT_INVALIDATE;
    protected boolean readThrough;
    protected CacheRebalanceMode rebalanceMode = DFLT_REBALANCE_MODE;
    protected int rebalanceOrder;
    protected long rebalanceTimeout = DFLT_REBALANCE_TIMEOUT;
    protected int rebalanceBatchSize = DFLT_REBALANCE_BATCH_SIZE;
    protected long rebalanceBatchesPrefetchCount = DFLT_REBALANCE_BATCHES_PREFETCH_COUNT;
    protected long rebalanceDelay;
    protected long rebalanceThrottle = DFLT_REBALANCE_THROTTLE;
    protected boolean writeThrough;
    protected boolean writeBehindEnabled = DFLT_WRITE_BEHIND_ENABLED;
    protected int writeBehindFlushSize = DFLT_WRITE_BEHIND_FLUSH_SIZE;
    protected long writeBehindFlushFrequency = DFLT_WRITE_BEHIND_FLUSH_FREQUENCY;
    protected int writeBehindFlushThreadCount = DFLT_WRITE_FROM_BEHIND_FLUSH_THREAD_CNT;
    protected int writeBehindBatchSize = DFLT_WRITE_BEHIND_BATCH_SIZE;
    protected boolean writeBehindCoalescing = DFLT_WRITE_BEHIND_COALESCING;
    protected CacheWriteSynchronizationMode writeSynchronizationMode = PRIMARY_SYNC;
    protected int maxQueryIteratorsCount = DFLT_MAX_QUERY_ITERATOR_CNT;
    protected int maxConcurrentAsyncOperations = DFLT_MAX_CONCURRENT_ASYNC_OPS;
    protected int queryDetailMetricsSize = DFLT_QRY_DETAIL_METRICS_SIZE;
    protected int queryParallelism = DFLT_QUERY_PARALLELISM;
    protected boolean readFromBackup = DFLT_READ_FROM_BACKUP;
    protected String sqlSchema;
    protected boolean sqlEscapeAll;
    protected int sqlIdxMaxInlineSize = DFLT_SQL_INDEX_MAX_INLINE_SIZE;
    protected boolean sqlOnheapCacheEnabled;
    protected int sqlOnheapCacheMaxSize = DFLT_SQL_ONHEAP_CACHE_MAX_SIZE;
    protected boolean copyOnRead = DFLT_COPY_ON_READ;
    protected PartitionLossPolicy partitionLossPolicy = DFLT_PARTITION_LOSS_POLICY;
    protected boolean statisticsEnabled;
    protected boolean storeByValue;
    protected Boolean storeKeepBinary = DFLT_STORE_KEEP_BINARY;
    protected int storeConcurrentLoadAllThreshold = DFLT_CONCURRENT_LOAD_ALL_THRESHOLD;
    protected boolean managementEnabled;
    protected boolean eventsDisabled = DFLT_EVENTS_DISABLED;
    protected long expireTime;

    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDataRegionName() {
        return dataRegionName;
    }

    public void setDataRegionName(String dataRegionName) {
        this.dataRegionName = dataRegionName;
    }

    public boolean isOnheapCacheEnabled() {
        return onheapCacheEnabled;
    }

    public void setOnheapCacheEnabled(boolean onheapCacheEnabled) {
        this.onheapCacheEnabled = onheapCacheEnabled;
    }

    public boolean isEagerTtl() {
        return eagerTtl;
    }

    public void setEagerTtl(boolean eagerTtl) {
        this.eagerTtl = eagerTtl;
    }

    public long getDefaultLockTimeout() {
        return defaultLockTimeout;
    }

    public void setDefaultLockTimeout(long defaultLockTimeout) {
        this.defaultLockTimeout = defaultLockTimeout;
    }

    public boolean isLoadPreviousValue() {
        return loadPreviousValue;
    }

    public void setLoadPreviousValue(boolean loadPreviousValue) {
        this.loadPreviousValue = loadPreviousValue;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public CacheAtomicityMode getAtomicityMode() {
        return atomicityMode;
    }

    public void setAtomicityMode(CacheAtomicityMode atomicityMode) {
        this.atomicityMode = atomicityMode;
    }

    public int getBackups() {
        return backups;
    }

    public void setBackups(int backups) {
        this.backups = backups;
    }

    public boolean isInvalidate() {
        return invalidate;
    }

    public void setInvalidate(boolean invalidate) {
        this.invalidate = invalidate;
    }

    public boolean isReadThrough() {
        return readThrough;
    }

    public void setReadThrough(boolean readThrough) {
        this.readThrough = readThrough;
    }

    public CacheRebalanceMode getRebalanceMode() {
        return rebalanceMode;
    }

    public void setRebalanceMode(CacheRebalanceMode rebalanceMode) {
        this.rebalanceMode = rebalanceMode;
    }

    public int getRebalanceOrder() {
        return rebalanceOrder;
    }

    public void setRebalanceOrder(int rebalanceOrder) {
        this.rebalanceOrder = rebalanceOrder;
    }

    public long getRebalanceTimeout() {
        return rebalanceTimeout;
    }

    public void setRebalanceTimeout(long rebalanceTimeout) {
        this.rebalanceTimeout = rebalanceTimeout;
    }

    public int getRebalanceBatchSize() {
        return rebalanceBatchSize;
    }

    public void setRebalanceBatchSize(int rebalanceBatchSize) {
        this.rebalanceBatchSize = rebalanceBatchSize;
    }

    public long getRebalanceBatchesPrefetchCount() {
        return rebalanceBatchesPrefetchCount;
    }

    public void setRebalanceBatchesPrefetchCount(long rebalanceBatchesPrefetchCount) {
        this.rebalanceBatchesPrefetchCount = rebalanceBatchesPrefetchCount;
    }

    public long getRebalanceDelay() {
        return rebalanceDelay;
    }

    public void setRebalanceDelay(long rebalanceDelay) {
        this.rebalanceDelay = rebalanceDelay;
    }

    public long getRebalanceThrottle() {
        return rebalanceThrottle;
    }

    public void setRebalanceThrottle(long rebalanceThrottle) {
        this.rebalanceThrottle = rebalanceThrottle;
    }

    public boolean isWriteThrough() {
        return writeThrough;
    }

    public void setWriteThrough(boolean writeThrough) {
        this.writeThrough = writeThrough;
    }

    public boolean isWriteBehindEnabled() {
        return writeBehindEnabled;
    }

    public void setWriteBehindEnabled(boolean writeBehindEnabled) {
        this.writeBehindEnabled = writeBehindEnabled;
    }

    public int getWriteBehindFlushSize() {
        return writeBehindFlushSize;
    }

    public void setWriteBehindFlushSize(int writeBehindFlushSize) {
        this.writeBehindFlushSize = writeBehindFlushSize;
    }

    public long getWriteBehindFlushFrequency() {
        return writeBehindFlushFrequency;
    }

    public void setWriteBehindFlushFrequency(long writeBehindFlushFrequency) {
        this.writeBehindFlushFrequency = writeBehindFlushFrequency;
    }

    public int getWriteBehindFlushThreadCount() {
        return writeBehindFlushThreadCount;
    }

    public void setWriteBehindFlushThreadCount(int writeBehindFlushThreadCount) {
        this.writeBehindFlushThreadCount = writeBehindFlushThreadCount;
    }

    public int getWriteBehindBatchSize() {
        return writeBehindBatchSize;
    }

    public void setWriteBehindBatchSize(int writeBehindBatchSize) {
        this.writeBehindBatchSize = writeBehindBatchSize;
    }

    public boolean isWriteBehindCoalescing() {
        return writeBehindCoalescing;
    }

    public void setWriteBehindCoalescing(boolean writeBehindCoalescing) {
        this.writeBehindCoalescing = writeBehindCoalescing;
    }

    public CacheWriteSynchronizationMode getWriteSynchronizationMode() {
        return writeSynchronizationMode;
    }

    public void setWriteSynchronizationMode(CacheWriteSynchronizationMode writeSynchronizationMode) {
        this.writeSynchronizationMode = writeSynchronizationMode;
    }

    public int getMaxQueryIteratorsCount() {
        return maxQueryIteratorsCount;
    }

    public void setMaxQueryIteratorsCount(int maxQueryIteratorsCount) {
        this.maxQueryIteratorsCount = maxQueryIteratorsCount;
    }

    public int getMaxConcurrentAsyncOperations() {
        return maxConcurrentAsyncOperations;
    }

    public void setMaxConcurrentAsyncOperations(int maxConcurrentAsyncOperations) {
        this.maxConcurrentAsyncOperations = maxConcurrentAsyncOperations;
    }

    public int getQueryDetailMetricsSize() {
        return queryDetailMetricsSize;
    }

    public void setQueryDetailMetricsSize(int queryDetailMetricsSize) {
        this.queryDetailMetricsSize = queryDetailMetricsSize;
    }

    public int getQueryParallelism() {
        return queryParallelism;
    }

    public void setQueryParallelism(int queryParallelism) {
        this.queryParallelism = queryParallelism;
    }

    public boolean isReadFromBackup() {
        return readFromBackup;
    }

    public void setReadFromBackup(boolean readFromBackup) {
        this.readFromBackup = readFromBackup;
    }

    public String getSqlSchema() {
        return sqlSchema;
    }

    public void setSqlSchema(String sqlSchema) {
        this.sqlSchema = sqlSchema;
    }

    public boolean isSqlEscapeAll() {
        return sqlEscapeAll;
    }

    public void setSqlEscapeAll(boolean sqlEscapeAll) {
        this.sqlEscapeAll = sqlEscapeAll;
    }

    public int getSqlIdxMaxInlineSize() {
        return sqlIdxMaxInlineSize;
    }

    public void setSqlIdxMaxInlineSize(int sqlIdxMaxInlineSize) {
        this.sqlIdxMaxInlineSize = sqlIdxMaxInlineSize;
    }

    public boolean isSqlOnheapCacheEnabled() {
        return sqlOnheapCacheEnabled;
    }

    public void setSqlOnheapCacheEnabled(boolean sqlOnheapCacheEnabled) {
        this.sqlOnheapCacheEnabled = sqlOnheapCacheEnabled;
    }

    public int getSqlOnheapCacheMaxSize() {
        return sqlOnheapCacheMaxSize;
    }

    public void setSqlOnheapCacheMaxSize(int sqlOnheapCacheMaxSize) {
        this.sqlOnheapCacheMaxSize = sqlOnheapCacheMaxSize;
    }

    public boolean isCopyOnRead() {
        return copyOnRead;
    }

    public void setCopyOnRead(boolean copyOnRead) {
        this.copyOnRead = copyOnRead;
    }

    public PartitionLossPolicy getPartitionLossPolicy() {
        return partitionLossPolicy;
    }

    public void setPartitionLossPolicy(PartitionLossPolicy partitionLossPolicy) {
        this.partitionLossPolicy = partitionLossPolicy;
    }

    public boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }

    public void setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
    }

    public boolean isStoreByValue() {
        return storeByValue;
    }

    public void setStoreByValue(boolean storeByValue) {
        this.storeByValue = storeByValue;
    }

    public Boolean getStoreKeepBinary() {
        return storeKeepBinary;
    }

    public void setStoreKeepBinary(Boolean storeKeepBinary) {
        this.storeKeepBinary = storeKeepBinary;
    }

    public int getStoreConcurrentLoadAllThreshold() {
        return storeConcurrentLoadAllThreshold;
    }

    public void setStoreConcurrentLoadAllThreshold(int storeConcurrentLoadAllThreshold) {
        this.storeConcurrentLoadAllThreshold = storeConcurrentLoadAllThreshold;
    }

    public boolean isManagementEnabled() {
        return managementEnabled;
    }

    public void setManagementEnabled(boolean managementEnabled) {
        this.managementEnabled = managementEnabled;
    }

    public boolean isEventsDisabled() {
        return eventsDisabled;
    }

    public void setEventsDisabled(boolean eventsDisabled) {
        this.eventsDisabled = eventsDisabled;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public void build(final Map<String, CacheConfiguration> configurations) {
        if (configurations == null) {
            return;
        }
        if (name != null) {
            for (String name : name) {
                if (name != null && !name.isEmpty()) {
                    configurations.put(name, build(name));
                }
            }
        } else {
            //匹配所有配置
            configurations.put("*", build("*"));
        }
    }

    protected CacheConfiguration build(final String name) {
        CacheConfiguration result = new CacheConfiguration();
        result.setAtomicityMode(atomicityMode);
        result.setBackups(backups);
        result.setCacheMode(cacheMode);
        result.setCopyOnRead(copyOnRead);
        result.setDataRegionName(dataRegionName);
        result.setDefaultLockTimeout(defaultLockTimeout);
        result.setEagerTtl(eagerTtl);
        result.setEventsDisabled(eventsDisabled);
        result.setGroupName(groupName);
        result.setInvalidate(invalidate);
        result.setLoadPreviousValue(loadPreviousValue);
        result.setManagementEnabled(managementEnabled);
        result.setMaxConcurrentAsyncOperations(maxConcurrentAsyncOperations);
        result.setMaxQueryIteratorsCount(maxQueryIteratorsCount);
        result.setQueryParallelism(queryParallelism);
        result.setQueryDetailMetricsSize(queryDetailMetricsSize);
        result.setName(name);
        result.setOnheapCacheEnabled(onheapCacheEnabled);
        result.setPartitionLossPolicy(partitionLossPolicy);
        result.setReadFromBackup(readFromBackup);
        result.setReadThrough(readThrough);
        result.setRebalanceBatchesPrefetchCount(rebalanceBatchesPrefetchCount);
        result.setRebalanceBatchSize(rebalanceBatchSize);
        result.setRebalanceDelay(rebalanceDelay);
        result.setRebalanceMode(rebalanceMode);
        result.setRebalanceOrder(rebalanceOrder);
        result.setRebalanceThrottle(rebalanceThrottle);
        result.setRebalanceTimeout(rebalanceTimeout);
        result.setSqlEscapeAll(sqlEscapeAll);
        result.setSqlIndexMaxInlineSize(sqlIdxMaxInlineSize);
        result.setSqlOnheapCacheEnabled(sqlOnheapCacheEnabled);
        result.setSqlOnheapCacheMaxSize(sqlOnheapCacheMaxSize);
        result.setSqlSchema(sqlSchema);
        result.setStatisticsEnabled(statisticsEnabled);
        result.setStoreByValue(storeByValue);
        result.setStoreConcurrentLoadAllThreshold(storeConcurrentLoadAllThreshold);
        result.setStoreKeepBinary(storeKeepBinary);
        result.setWriteBehindBatchSize(writeBehindBatchSize);
        result.setWriteBehindCoalescing(writeBehindCoalescing);
        result.setWriteBehindEnabled(writeBehindEnabled);
        result.setWriteBehindFlushFrequency(writeBehindFlushFrequency);
        result.setWriteBehindFlushSize(writeBehindFlushSize);
        result.setWriteBehindFlushThreadCount(writeBehindFlushThreadCount);
        result.setWriteSynchronizationMode(writeSynchronizationMode);
        result.setWriteThrough(writeThrough);
        result.setExpiryPolicyFactory(expireTime <= 0 ? null : CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.MICROSECONDS, expireTime)));
        return result;
    }

}

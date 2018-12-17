package com.jd.laf.ignite.spring;

import org.apache.ignite.binary.BinaryTypeConfiguration;
import org.apache.ignite.configuration.BinaryConfiguration;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.plugin.segmentation.SegmentationPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import static org.apache.ignite.configuration.IgniteConfiguration.*;

@ConfigurationProperties(prefix = "ignite")
public class IgniteProperties {

    protected String igniteInstanceName;
    protected int publicThreadPoolSize = DFLT_PUBLIC_THREAD_CNT;
    protected Integer serviceThreadPoolSize;
    protected int asyncCallbackPoolSize = DFLT_PUBLIC_THREAD_CNT;
    protected int stripedPoolSize = DFLT_PUBLIC_THREAD_CNT;
    protected int systemThreadPoolSize = DFLT_SYSTEM_CORE_THREAD_CNT;
    protected int managementThreadPoolSize = DFLT_MGMT_THREAD_CNT;
    protected int igfsThreadPoolSize = AVAILABLE_PROC_CNT;
    protected int dataStreamerThreadPoolSize = DFLT_DATA_STREAMER_POOL_SIZE;
    protected int utilityCachePoolSize = DFLT_SYSTEM_CORE_THREAD_CNT;
    protected long utilityCacheKeepAliveTime = DFLT_THREAD_KEEP_ALIVE_TIME;
    protected int p2pThreadPoolSize = DFLT_P2P_THREAD_CNT;
    protected int queryThreadPoolSize = DFLT_QUERY_THREAD_POOL_SIZE;
    protected String igniteHome;
    protected String workDirectory;
    protected boolean marshalLocalJobs = DFLT_MARSHAL_LOCAL_JOBS;
    protected boolean daemon;
    protected boolean p2pEnabled = DFLT_P2P_ENABLED;
    protected String[] p2pLocalClassPathExclude;
    protected int[] includeEventTypes;
    protected long networkTimeout = DFLT_NETWORK_TIMEOUT;
    protected long networkSendRetryDelay = DFLT_SEND_RETRY_DELAY;
    protected int networkSendRetryCount = DFLT_SEND_RETRY_CNT;
    protected int metricsHistorySize = DFLT_METRICS_HISTORY_SIZE;
    protected long metricsUpdateFrequency = DFLT_METRICS_UPDATE_FREQ;
    protected long metricsExpireTime = DFLT_METRICS_EXPIRE_TIME;
    protected SegmentationPolicy segmentationPolicy = DFLT_SEG_PLC;
    protected int segmentationResolveAttempts = DFLT_SEG_RESOLVE_ATTEMPTS;
    protected boolean waitForSegmentOnStart = DFLT_WAIT_FOR_SEG_ON_START;
    protected boolean allSegmentationResolversPassRequired = DFLT_ALL_SEG_RESOLVERS_PASS_REQ;
    protected long segmentCheckFrequency = DFLT_SEG_CHK_FREQ;
    protected boolean clientMode;
    protected int rebalanceThreadPoolSize = DFLT_REBALANCE_THREAD_POOL_SIZE;
    protected boolean cacheSanityCheckEnabled = DFLT_CACHE_SANITY_CHECK_ENABLED;
    protected DeploymentMode deploymentMode = DFLT_DEPLOYMENT_MODE;
    protected int p2pMissedCacheSize = DFLT_P2P_MISSED_RESOURCES_CACHE_SIZE;
    protected String localHost;
    protected int timeServerPortBase = DFLT_TIME_SERVER_PORT_BASE;
    protected int timeServerPortRange = DFLT_TIME_SERVER_PORT_RANGE;
    protected long failureDetectionTimeout = DFLT_FAILURE_DETECTION_TIMEOUT;
    protected long clientFailureDetectionTimeout = DFLT_CLIENT_FAILURE_DETECTION_TIMEOUT;
    protected String[] includeProperties;
    protected long metricsLogFrequency = DFLT_METRICS_LOG_FREQ;
    protected boolean activeOnStart = DFLT_ACTIVE_ON_START;
    protected boolean autoActivationEnabled = DFLT_AUTO_ACTIVATION;
    protected long longQueryWarningTimeout = DFLT_LONG_QRY_WARN_TIMEOUT;
    protected boolean authenticationEnabled;

    protected DataStorageProperties storage;
    protected List<CacheProperties> caches;
    protected List<BinaryTypeProperties> binaryTypes;

    public String getIgniteInstanceName() {
        return igniteInstanceName;
    }

    public void setIgniteInstanceName(String igniteInstanceName) {
        this.igniteInstanceName = igniteInstanceName;
    }

    public int getPublicThreadPoolSize() {
        return publicThreadPoolSize;
    }

    public void setPublicThreadPoolSize(int publicThreadPoolSize) {
        this.publicThreadPoolSize = publicThreadPoolSize;
    }

    public Integer getServiceThreadPoolSize() {
        return serviceThreadPoolSize;
    }

    public void setServiceThreadPoolSize(Integer serviceThreadPoolSize) {
        this.serviceThreadPoolSize = serviceThreadPoolSize;
    }

    public int getAsyncCallbackPoolSize() {
        return asyncCallbackPoolSize;
    }

    public void setAsyncCallbackPoolSize(int asyncCallbackPoolSize) {
        this.asyncCallbackPoolSize = asyncCallbackPoolSize;
    }

    public int getStripedPoolSize() {
        return stripedPoolSize;
    }

    public void setStripedPoolSize(int stripedPoolSize) {
        this.stripedPoolSize = stripedPoolSize;
    }

    public int getSystemThreadPoolSize() {
        return systemThreadPoolSize;
    }

    public void setSystemThreadPoolSize(int systemThreadPoolSize) {
        this.systemThreadPoolSize = systemThreadPoolSize;
    }

    public int getManagementThreadPoolSize() {
        return managementThreadPoolSize;
    }

    public void setManagementThreadPoolSize(int managementThreadPoolSize) {
        this.managementThreadPoolSize = managementThreadPoolSize;
    }

    public int getIgfsThreadPoolSize() {
        return igfsThreadPoolSize;
    }

    public void setIgfsThreadPoolSize(int igfsThreadPoolSize) {
        this.igfsThreadPoolSize = igfsThreadPoolSize;
    }

    public int getDataStreamerThreadPoolSize() {
        return dataStreamerThreadPoolSize;
    }

    public void setDataStreamerThreadPoolSize(int dataStreamerThreadPoolSize) {
        this.dataStreamerThreadPoolSize = dataStreamerThreadPoolSize;
    }

    public int getUtilityCachePoolSize() {
        return utilityCachePoolSize;
    }

    public void setUtilityCachePoolSize(int utilityCachePoolSize) {
        this.utilityCachePoolSize = utilityCachePoolSize;
    }

    public long getUtilityCacheKeepAliveTime() {
        return utilityCacheKeepAliveTime;
    }

    public void setUtilityCacheKeepAliveTime(long utilityCacheKeepAliveTime) {
        this.utilityCacheKeepAliveTime = utilityCacheKeepAliveTime;
    }

    public int getP2pThreadPoolSize() {
        return p2pThreadPoolSize;
    }

    public void setP2pThreadPoolSize(int p2pThreadPoolSize) {
        this.p2pThreadPoolSize = p2pThreadPoolSize;
    }

    public int getQueryThreadPoolSize() {
        return queryThreadPoolSize;
    }

    public void setQueryThreadPoolSize(int queryThreadPoolSize) {
        this.queryThreadPoolSize = queryThreadPoolSize;
    }

    public String getIgniteHome() {
        return igniteHome;
    }

    public void setIgniteHome(String igniteHome) {
        this.igniteHome = igniteHome;
    }

    public String getWorkDirectory() {
        return workDirectory;
    }

    public void setWorkDirectory(String workDirectory) {
        this.workDirectory = workDirectory;
    }

    public boolean isMarshalLocalJobs() {
        return marshalLocalJobs;
    }

    public void setMarshalLocalJobs(boolean marshalLocalJobs) {
        this.marshalLocalJobs = marshalLocalJobs;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public boolean isP2pEnabled() {
        return p2pEnabled;
    }

    public void setP2pEnabled(boolean p2pEnabled) {
        this.p2pEnabled = p2pEnabled;
    }

    public String[] getP2pLocalClassPathExclude() {
        return p2pLocalClassPathExclude;
    }

    public void setP2pLocalClassPathExclude(String[] p2pLocalClassPathExclude) {
        this.p2pLocalClassPathExclude = p2pLocalClassPathExclude;
    }

    public int[] getIncludeEventTypes() {
        return includeEventTypes;
    }

    public void setIncludeEventTypes(int[] includeEventTypes) {
        this.includeEventTypes = includeEventTypes;
    }

    public long getNetworkTimeout() {
        return networkTimeout;
    }

    public void setNetworkTimeout(long networkTimeout) {
        this.networkTimeout = networkTimeout;
    }

    public long getNetworkSendRetryDelay() {
        return networkSendRetryDelay;
    }

    public void setNetworkSendRetryDelay(long networkSendRetryDelay) {
        this.networkSendRetryDelay = networkSendRetryDelay;
    }

    public int getNetworkSendRetryCount() {
        return networkSendRetryCount;
    }

    public void setNetworkSendRetryCount(int networkSendRetryCount) {
        this.networkSendRetryCount = networkSendRetryCount;
    }

    public int getMetricsHistorySize() {
        return metricsHistorySize;
    }

    public void setMetricsHistorySize(int metricsHistorySize) {
        this.metricsHistorySize = metricsHistorySize;
    }

    public long getMetricsUpdateFrequency() {
        return metricsUpdateFrequency;
    }

    public void setMetricsUpdateFrequency(long metricsUpdateFrequency) {
        this.metricsUpdateFrequency = metricsUpdateFrequency;
    }

    public long getMetricsExpireTime() {
        return metricsExpireTime;
    }

    public void setMetricsExpireTime(long metricsExpireTime) {
        this.metricsExpireTime = metricsExpireTime;
    }

    public SegmentationPolicy getSegmentationPolicy() {
        return segmentationPolicy;
    }

    public void setSegmentationPolicy(SegmentationPolicy segmentationPolicy) {
        this.segmentationPolicy = segmentationPolicy;
    }

    public int getSegmentationResolveAttempts() {
        return segmentationResolveAttempts;
    }

    public void setSegmentationResolveAttempts(int segmentationResolveAttempts) {
        this.segmentationResolveAttempts = segmentationResolveAttempts;
    }

    public boolean isWaitForSegmentOnStart() {
        return waitForSegmentOnStart;
    }

    public void setWaitForSegmentOnStart(boolean waitForSegmentOnStart) {
        this.waitForSegmentOnStart = waitForSegmentOnStart;
    }

    public boolean isAllSegmentationResolversPassRequired() {
        return allSegmentationResolversPassRequired;
    }

    public void setAllSegmentationResolversPassRequired(boolean allSegmentationResolversPassRequired) {
        this.allSegmentationResolversPassRequired = allSegmentationResolversPassRequired;
    }

    public long getSegmentCheckFrequency() {
        return segmentCheckFrequency;
    }

    public void setSegmentCheckFrequency(long segmentCheckFrequency) {
        this.segmentCheckFrequency = segmentCheckFrequency;
    }

    public boolean getClientMode() {
        return clientMode;
    }

    public void setClientMode(boolean clientMode) {
        this.clientMode = clientMode;
    }

    public int getRebalanceThreadPoolSize() {
        return rebalanceThreadPoolSize;
    }

    public void setRebalanceThreadPoolSize(int rebalanceThreadPoolSize) {
        this.rebalanceThreadPoolSize = rebalanceThreadPoolSize;
    }

    public boolean isCacheSanityCheckEnabled() {
        return cacheSanityCheckEnabled;
    }

    public void setCacheSanityCheckEnabled(boolean cacheSanityCheckEnabled) {
        this.cacheSanityCheckEnabled = cacheSanityCheckEnabled;
    }

    public DeploymentMode getDeploymentMode() {
        return deploymentMode;
    }

    public void setDeploymentMode(DeploymentMode deploymentMode) {
        this.deploymentMode = deploymentMode;
    }

    public int getP2pMissedCacheSize() {
        return p2pMissedCacheSize;
    }

    public void setP2pMissedCacheSize(int p2pMissedCacheSize) {
        this.p2pMissedCacheSize = p2pMissedCacheSize;
    }

    public String getLocalHost() {
        return localHost;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public int getTimeServerPortBase() {
        return timeServerPortBase;
    }

    public void setTimeServerPortBase(int timeServerPortBase) {
        this.timeServerPortBase = timeServerPortBase;
    }

    public int getTimeServerPortRange() {
        return timeServerPortRange;
    }

    public void setTimeServerPortRange(int timeServerPortRange) {
        this.timeServerPortRange = timeServerPortRange;
    }

    public long getFailureDetectionTimeout() {
        return failureDetectionTimeout;
    }

    public void setFailureDetectionTimeout(long failureDetectionTimeout) {
        this.failureDetectionTimeout = failureDetectionTimeout;
    }

    public long getClientFailureDetectionTimeout() {
        return clientFailureDetectionTimeout;
    }

    public void setClientFailureDetectionTimeout(long clientFailureDetectionTimeout) {
        this.clientFailureDetectionTimeout = clientFailureDetectionTimeout;
    }

    public String[] getIncludeProperties() {
        return includeProperties;
    }

    public void setIncludeProperties(String[] includeProperties) {
        this.includeProperties = includeProperties;
    }

    public long getMetricsLogFrequency() {
        return metricsLogFrequency;
    }

    public void setMetricsLogFrequency(long metricsLogFrequency) {
        this.metricsLogFrequency = metricsLogFrequency;
    }

    public boolean isActiveOnStart() {
        return activeOnStart;
    }

    public void setActiveOnStart(boolean activeOnStart) {
        this.activeOnStart = activeOnStart;
    }

    public boolean isAutoActivationEnabled() {
        return autoActivationEnabled;
    }

    public void setAutoActivationEnabled(boolean autoActivationEnabled) {
        this.autoActivationEnabled = autoActivationEnabled;
    }

    public long getLongQueryWarningTimeout() {
        return longQueryWarningTimeout;
    }

    public void setLongQueryWarningTimeout(long longQueryWarningTimeout) {
        this.longQueryWarningTimeout = longQueryWarningTimeout;
    }

    public boolean isAuthenticationEnabled() {
        return authenticationEnabled;
    }

    public void setAuthenticationEnabled(boolean authenticationEnabled) {
        this.authenticationEnabled = authenticationEnabled;
    }

    public DataStorageProperties getStorage() {
        return storage;
    }

    public void setStorage(DataStorageProperties storage) {
        this.storage = storage;
    }

    public List<CacheProperties> getCaches() {
        return caches;
    }

    public void setCaches(List<CacheProperties> caches) {
        this.caches = caches;
    }

    public List<BinaryTypeProperties> getBinaryTypes() {
        return binaryTypes;
    }

    public void setBinaryTypes(List<BinaryTypeProperties> binaryTypes) {
        this.binaryTypes = binaryTypes;
    }

    public IgniteConfiguration build() throws Exception {
        IgniteConfiguration result = new IgniteConfiguration();
        result.setIgniteHome(igniteHome);
        result.setIgniteInstanceName(igniteInstanceName);
        result.setPublicThreadPoolSize(publicThreadPoolSize);
        if (serviceThreadPoolSize != null) {
            result.setServiceThreadPoolSize(serviceThreadPoolSize);
        }
        result.setAsyncCallbackPoolSize(asyncCallbackPoolSize);
        result.setStripedPoolSize(stripedPoolSize);
        result.setSystemThreadPoolSize(systemThreadPoolSize);
        result.setManagementThreadPoolSize(managementThreadPoolSize);
        result.setIgfsThreadPoolSize(igfsThreadPoolSize);
        result.setDataStreamerThreadPoolSize(dataStreamerThreadPoolSize);
        result.setUtilityCachePoolSize(utilityCachePoolSize);
        result.setUtilityCacheKeepAliveTime(utilityCacheKeepAliveTime);
        result.setPeerClassLoadingThreadPoolSize(p2pThreadPoolSize);
        result.setPeerClassLoadingEnabled(p2pEnabled);
        result.setPeerClassLoadingLocalClassPathExclude(p2pLocalClassPathExclude);
        result.setPeerClassLoadingMissedResourcesCacheSize(p2pMissedCacheSize);
        result.setQueryThreadPoolSize(queryThreadPoolSize);
        result.setWorkDirectory(workDirectory);
        result.setMarshalLocalJobs(marshalLocalJobs);
        result.setDaemon(daemon);
        result.setIncludeEventTypes(includeEventTypes);
        result.setNetworkTimeout(networkTimeout);
        result.setNetworkSendRetryDelay(networkSendRetryDelay);
        result.setNetworkSendRetryCount(networkSendRetryCount);
        result.setMetricsExpireTime(metricsExpireTime);
        result.setMetricsUpdateFrequency(metricsUpdateFrequency);
        result.setMetricsHistorySize(metricsHistorySize);
        result.setMetricsLogFrequency(metricsLogFrequency);
        result.setSegmentationPolicy(segmentationPolicy);
        result.setSegmentCheckFrequency(segmentCheckFrequency);
        result.setSegmentationResolveAttempts(segmentationResolveAttempts);
        result.setWaitForSegmentOnStart(waitForSegmentOnStart);
        result.setAllSegmentationResolversPassRequired(allSegmentationResolversPassRequired);
        result.setClientMode(clientMode);
        result.setRebalanceThreadPoolSize(rebalanceThreadPoolSize);
        result.setCacheSanityCheckEnabled(cacheSanityCheckEnabled);
        result.setDeploymentMode(deploymentMode);
        result.setLocalHost(localHost == null || localHost.isEmpty() ? NetUtils.getLocalHost() : localHost);
        result.setTimeServerPortBase(timeServerPortBase);
        result.setTimeServerPortRange(timeServerPortRange);
        result.setFailureDetectionTimeout(failureDetectionTimeout);
        result.setClientFailureDetectionTimeout(clientFailureDetectionTimeout);
        result.setIncludeProperties(includeProperties);
        result.setActiveOnStart(activeOnStart);
        result.setAutoActivationEnabled(autoActivationEnabled);
        result.setLongQueryWarningTimeout(longQueryWarningTimeout);
        result.setAuthenticationEnabled(authenticationEnabled);

        if (storage != null) {
            result.setDataStorageConfiguration(storage.build());
        }
        if (caches != null) {
            CacheConfiguration[] configurations = new CacheConfiguration[caches.size()];
            int i = 0;
            for (CacheProperties cache : caches) {
                configurations[i++] = cache.build();
            }
            result.setCacheConfiguration(configurations);
        }
        if (binaryTypes != null) {
            BinaryTypeConfiguration binaryTypeCfg;
            BinaryConfiguration binaryCfg = new BinaryConfiguration();
            List<BinaryTypeConfiguration> binaryTypeCfgs = new ArrayList<>(binaryTypes.size());
            for (BinaryTypeProperties binaryType : binaryTypes) {
                if (binaryType.getTypeName() == null || binaryType.getTypeName().isEmpty()) {
                    //默认
                    build(binaryCfg, binaryType);
                } else {
                    //指定类型
                    binaryTypeCfg = build(binaryType);
                    if (binaryTypeCfg.getIdMapper() != null
                            || binaryTypeCfg.getNameMapper() != null
                            || binaryTypeCfg.getSerializer() != null) {
                        binaryTypeCfgs.add(binaryTypeCfg);
                    }
                }
            }
            if (!binaryTypeCfgs.isEmpty()) {
                binaryCfg.setTypeConfigurations(binaryTypeCfgs);
            }
            if (binaryCfg.getIdMapper() != null
                    || binaryCfg.getNameMapper() != null
                    || binaryCfg.getSerializer() != null
                    || binaryCfg.getTypeConfigurations() != null) {
                result.setBinaryConfiguration(binaryCfg);
            }
        }

        return result;
    }

    protected BinaryTypeConfiguration build(final BinaryTypeProperties binaryType) throws InstantiationException, IllegalAccessException {
        BinaryTypeConfiguration binaryTypeCfg = new BinaryTypeConfiguration();
        if (binaryType.getIdMapperClass() != null) {
            binaryTypeCfg.setIdMapper(binaryType.getIdMapperClass().newInstance());
        }
        if (binaryType.getNameMapperClass() != null) {
            binaryTypeCfg.setNameMapper(binaryType.getNameMapperClass().newInstance());
        }
        if (binaryType.getSerializerClass() != null) {
            binaryTypeCfg.setSerializer(binaryType.getSerializerClass().newInstance());
        }
        return binaryTypeCfg;
    }

    protected void build(final BinaryConfiguration binaryCfg, final BinaryTypeProperties binaryType) throws InstantiationException, IllegalAccessException {
        if (binaryType.getIdMapperClass() != null) {
            binaryCfg.setIdMapper(binaryType.getIdMapperClass().newInstance());
        }
        if (binaryType.getNameMapperClass() != null) {
            binaryCfg.setNameMapper(binaryType.getNameMapperClass().newInstance());
        }
        if (binaryType.getSerializerClass() != null) {
            binaryCfg.setSerializer(binaryType.getSerializerClass().newInstance());
        }
    }
}

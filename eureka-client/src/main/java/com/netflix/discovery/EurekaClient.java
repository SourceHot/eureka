package com.netflix.discovery;

import com.google.inject.ImplementedBy;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.HealthCheckCallback;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.LookupService;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Define a simple interface over the current DiscoveryClient implementation.
 * <p>
 * This interface does NOT try to clean up the current client interface for eureka 1.x. Rather it tries
 * to provide an easier transition path from eureka 1.x to eureka 2.x.
 * <p>
 * EurekaClient API contracts are:
 * - provide the ability to get InstanceInfo(s) (in various different ways)
 * - provide the ability to get data about the local Client (known regions, own AZ etc)
 * - provide the ability to register and access the healthcheck handler for the client
 *
 * @author David Liu
 * <p>
 * eureka客户端
 */
@ImplementedBy(DiscoveryClient.class)
public interface EurekaClient extends LookupService {

    // ========================
    // getters for InstanceInfo
    // ========================

    /**
     * 根据分区获取应用集合
     *
     * @param region the region that the Applications reside in
     * @return an {@link com.netflix.discovery.shared.Applications} for the matching region. a Null value
     * is treated as the local region.
     */
    Applications getApplicationsForARegion(@Nullable String region);

    /**
     * Get all applications registered with a specific eureka service.
     * <p>
     * 根据服务路由获取应用集合
     *
     * @param serviceUrl The string representation of the service url.
     * @return The registry information containing all applications.
     */
    Applications getApplications(String serviceUrl);

    /**
     * Gets the list of instances matching the given VIP Address.
     * <p>
     * 根据VIP地址获取实例信息集合
     *
     * @param vipAddress The VIP address to match the instances for.
     * @param secure     true if it is a secure vip address, false otherwise
     * @return - The list of {@link InstanceInfo} objects matching the criteria
     */
    List<InstanceInfo> getInstancesByVipAddress(String vipAddress, boolean secure);

    /**
     * Gets the list of instances matching the given VIP Address in the passed region.
     * 根据VIP地址和区域获取实例信息集合
     *
     * @param vipAddress The VIP address to match the instances for.
     * @param secure     true if it is a secure vip address, false otherwise
     * @param region     region from which the instances are to be fetched. If <code>null</code> then local region is
     *                   assumed.
     * @return - The list of {@link InstanceInfo} objects matching the criteria, empty list if not instances found.
     */
    List<InstanceInfo> getInstancesByVipAddress(String vipAddress, boolean secure, @Nullable String region);

    /**
     * Gets the list of instances matching the given VIP Address and the given
     * application name if both of them are not null. If one of them is null,
     * then that criterion is completely ignored for matching instances.
     * 根据VIP地址和应用名称获取实例信息集合
     *
     * @param vipAddress The VIP address to match the instances for.
     * @param appName    The applicationName to match the instances for.
     * @param secure     true if it is a secure vip address, false otherwise.
     * @return - The list of {@link InstanceInfo} objects matching the criteria.
     */
    List<InstanceInfo> getInstancesByVipAddressAndAppName(String vipAddress, String appName, boolean secure);

    // ==========================
    // getters for local metadata
    // ==========================

    /**
     * 获取已知的区域
     *
     * @return in String form all regions (local + remote) that can be accessed by this client
     */
    Set<String> getAllKnownRegions();

    /**
     * 获取当前eureka实例状态
     *
     * @return the current self instance status as seen on the Eureka server.
     */
    InstanceInfo.InstanceStatus getInstanceRemoteStatus();

    /**
     * 根据区域获取eureka服务路由列表
     *
     * @param zone the zone in which the client resides
     * @return The list of all eureka service urls for the eureka client to talk to.
     * @deprecated see {@link com.netflix.discovery.endpoint.EndpointUtils} for replacement
     * <p>
     * Get the list of all eureka service urls for the eureka client to talk to.
     */
    @Deprecated
    List<String> getDiscoveryServiceUrls(String zone);

    /**
     * 根据区域获取eureka服务路由列表
     *
     * @param instanceZone   The zone in which the client resides
     * @param preferSameZone true if we have to prefer the same zone as the client, false otherwise
     * @return The list of all eureka service urls for the eureka client to talk to
     * @deprecated see {@link com.netflix.discovery.endpoint.EndpointUtils} for replacement
     * <p>
     * Get the list of all eureka service urls from properties file for the eureka client to talk to.
     */
    @Deprecated
    List<String> getServiceUrlsFromConfig(String instanceZone, boolean preferSameZone);

    /**
     * 根据区域获取eureka服务路由列表
     *
     * @param instanceZone   The zone in which the client resides.
     * @param preferSameZone true if we have to prefer the same zone as the client, false otherwise.
     * @return The list of all eureka service urls for the eureka client to talk to.
     * @deprecated see {@link com.netflix.discovery.endpoint.EndpointUtils} for replacement
     * <p>
     * Get the list of all eureka service urls from DNS for the eureka client to
     * talk to. The client picks up the service url from its zone and then fails over to
     * other zones randomly. If there are multiple servers in the same zone, the client once
     * again picks one randomly. This way the traffic will be distributed in the case of failures.
     */
    @Deprecated
    List<String> getServiceUrlsFromDNS(String instanceZone, boolean preferSameZone);

    // ===========================
    // healthcheck related methods
    // ===========================

    /**
     * 注册心跳检查的回调实例
     *
     * @param callback app specific healthcheck.
     * @deprecated Use {@link #registerHealthCheck(com.netflix.appinfo.HealthCheckHandler)} instead.
     * <p>
     * Register {@link HealthCheckCallback} with the eureka client.
     * <p>
     * Once registered, the eureka client will invoke the
     * {@link HealthCheckCallback} in intervals specified by
     * {@link EurekaClientConfig#getInstanceInfoReplicationIntervalSeconds()}.
     */
    @Deprecated
    void registerHealthCheckCallback(HealthCheckCallback callback);

    /**
     * 注册HealthCheckHandler接口
     * Register {@link HealthCheckHandler} with the eureka client.
     * <p>
     * Once registered, the eureka client will first make an onDemand update of the
     * registering instanceInfo by calling the newly registered healthcheck handler,
     * and subsequently invoke the {@link HealthCheckHandler} in intervals specified
     * by {@link EurekaClientConfig#getInstanceInfoReplicationIntervalSeconds()}.
     *
     * @param healthCheckHandler app specific healthcheck handler.
     */
    void registerHealthCheck(HealthCheckHandler healthCheckHandler);

    /**
     * 注册EurekaEventListener
     * Register {@link EurekaEventListener} with the eureka client.
     * <p>
     * Once registered, the eureka client will invoke {@link EurekaEventListener#onEvent}
     * whenever there is a change in eureka client's internal state.  Use this instead of
     * polling the client for changes.
     * <p>
     * {@link EurekaEventListener#onEvent} is called from the context of an internal thread
     * and must therefore return as quickly as possible without blocking.
     *
     * @param eventListener
     */
    void registerEventListener(EurekaEventListener eventListener);

    /**
     * 取消EurekaEventListener注册
     * Unregister a {@link EurekaEventListener} previous registered with {@link EurekaClient#registerEventListener}
     * or injected into the constructor of {@link DiscoveryClient}
     *
     * @param eventListener
     * @return True if removed otherwise false if the listener was never registered.
     */
    boolean unregisterEventListener(EurekaEventListener eventListener);

    /**
     * 获取HealthCheckHandler
     *
     * @return the current registered healthcheck handler
     */
    HealthCheckHandler getHealthCheckHandler();

    // =============
    // other methods
    // =============

    /**
     * 关闭eureka客户端。
     * Shuts down Eureka Client. Also sends a deregistration request to the eureka server.
     */
    void shutdown();

    /**
     * 获取eureka客户端配置
     *
     * @return the configuration of this eureka client
     */
    EurekaClientConfig getEurekaClientConfig();

    /**
     * 获取应用信息管理器
     *
     * @return the application info manager of this eureka client
     */
    ApplicationInfoManager getApplicationInfoManager();
}

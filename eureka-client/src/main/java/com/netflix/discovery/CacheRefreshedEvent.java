package com.netflix.discovery;

/**
 * This event is sent by {@link EurekaClient) whenever it has refreshed its local 
 * local cache with information received from the Eureka server.
 *
 *
 * 缓存刷新事件
 * @author brenuart
 */
public class CacheRefreshedEvent extends DiscoveryEvent {
    @Override
    public String toString() {
        return "CacheRefreshedEvent[timestamp=" + getTimestamp() + "]";
    }
}

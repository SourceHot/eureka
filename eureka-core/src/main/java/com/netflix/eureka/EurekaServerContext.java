/*
 * Copyright 2015 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.netflix.eureka;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;

/**
 * eureka服务上下文
 * @author David Liu
 */
public interface EurekaServerContext {

    /**
     * 初始化
     * @throws Exception
     */
    void initialize() throws Exception;

    /**
     * 关闭
     * @throws Exception
     */
    void shutdown() throws Exception;

    /**
     * 获取eureka服务配置
     * @return
     */
    EurekaServerConfig getServerConfig();

    /**
     * 获取eureka节点集合
     * @return
     */
    PeerEurekaNodes getPeerEurekaNodes();

    /**
     * 服务编码器
     * @return
     */
    ServerCodecs getServerCodecs();

    PeerAwareInstanceRegistry getRegistry();

    ApplicationInfoManager getApplicationInfoManager();

}

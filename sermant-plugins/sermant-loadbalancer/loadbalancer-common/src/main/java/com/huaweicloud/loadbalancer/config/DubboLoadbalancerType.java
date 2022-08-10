/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huaweicloud.loadbalancer.config;

/**
 * dubbo负载均衡策略
 *
 * @author provenceee
 * @since 2022-01-21
 */
public enum DubboLoadbalancerType {
    /**
     * 随机
     */
    RANDOM("Random"),

    /**
     * 轮询
     */
    ROUND_ROBIN("RoundRobin"),

    /**
     * 最少活跃
     */
    LEAST_ACTIVE("LeastActive"),

    /**
     * 一致性HASH
     */
    CONSIST_ENT_HASH("ConsistEntHash"),

    /**
     * 最短响应时间（仅支持dubbo 2.7.7+）
     */
    SHORTEST_RESPONSE("ShortestResponse");

    /**
     * 实际配置映射名称
     */
    private final String mapperName;

    DubboLoadbalancerType(String mapperName) {
        this.mapperName = mapperName;
    }

    public String getMapperName() {
        return mapperName;
    }
}

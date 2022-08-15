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

package com.huawei.loadbalancer.cache;

import com.huaweicloud.loadbalancer.config.LbContext;
import com.huaweicloud.loadbalancer.config.RibbonLoadbalancerType;
import com.huaweicloud.loadbalancer.rule.LoadbalancerRule;
import com.huaweicloud.loadbalancer.rule.RuleManager;
import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEventType;

import com.netflix.loadbalancer.IRule;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * ribbon loadbalancer负载均衡缓存
 *
 * @author zhouss
 * @since 2022-08-12
 */
public enum RibbonLoadbalancerCache {
    /**
     * 单例
     */
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger();

    /**
     * 新负载均衡缓存
     * <pre>
     *     key: service name
     *     value: 负载均衡类型
     * </pre>
     */
    private final Map<String, RibbonLoadbalancerType> newTypeCache = new ConcurrentHashMap<>();

    /**
     * 原始负载均衡;Ribbon负载均衡不会关联服务名, 若用户为配置负载均衡key, 则全局仅一个。若用户已使用自身的负载均衡key, 则不会支持
     */
    private RibbonLoadbalancerType originType;

    RibbonLoadbalancerCache() {
        RuleManager.INSTANCE.addRuleListener(this::updateCache);
    }

    private void updateCache(LoadbalancerRule rule, DynamicConfigEvent event) {
        if (rule.getRule() == null || !LbContext.INSTANCE.isTargetLb(LbContext.LOADBALANCER_RIBBON)) {
            return;
        }
        final Optional<RibbonLoadbalancerType> ribbonLoadbalancerType = RibbonLoadbalancerType
                .matchLoadbalancer(rule.getRule());
        if (!ribbonLoadbalancerType.isPresent()) {
            LOGGER.fine(String.format(Locale.ENGLISH, "Can not support ribbon loadbalancer rule: [%s]",
                    rule.getRule()));
            return;
        }
        final String serviceName = rule.getServiceName();
        if (serviceName == null) {
            newTypeCache.clear();
            return;
        }
        if (event.getEventType() == DynamicConfigEventType.DELETE) {
            newTypeCache.put(serviceName, originType);
        } else {
            newTypeCache.put(serviceName, ribbonLoadbalancerType.get());
        }
    }

    /**
     * 存放负载均衡类型
     *
     * @param serviceName 服务名
     * @param loadbalancerType 负载均衡类型
     * @see IRule
     */
    public void put(String serviceName, RibbonLoadbalancerType loadbalancerType) {
        newTypeCache.put(serviceName, loadbalancerType);
    }

    /**
     * 获取指定服务名的缓存负载均衡类型
     *
     * @param serviceName 缓存
     * @return 负载均衡
     */
    public RibbonLoadbalancerType getTargetServiceLbType(String serviceName) {
        return newTypeCache.get(serviceName);
    }

    public void setOriginType(RibbonLoadbalancerType originType) {
        this.originType = originType;
    }
}

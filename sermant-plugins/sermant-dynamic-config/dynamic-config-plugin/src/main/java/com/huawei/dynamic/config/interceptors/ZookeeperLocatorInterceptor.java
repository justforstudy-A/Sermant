/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.dynamic.config.interceptors;

import com.huawei.dynamic.config.ConfigHolder;
import com.huawei.dynamic.config.DynamicConfiguration;
import com.huawei.dynamic.config.source.OriginConfigDisableSource;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

import org.springframework.core.env.CompositePropertySource;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 拦截loadFactories注入自定义配置源
 *
 * @author zhouss
 * @since 2022-04-08
 */
public class ZookeeperLocatorInterceptor extends DynamicConfigSwitchSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final DynamicConfiguration configuration;

    /**
     * 构造器
     */
    public ZookeeperLocatorInterceptor() {
        configuration = PluginConfigManager.getPluginConfig(DynamicConfiguration.class);
    }

    @Override
    public ExecuteContext doBefore(ExecuteContext context) {
        LOGGER.log(Level.SEVERE, "==============dynamicClose:" + isDynamicClosed() + "=========");
        if (!configuration.isEnableOriginConfigCenter() || isDynamicClosed()) {
            context.skip(new CompositePropertySource("Empty"));
        }
        return context;
    }

    /**
     * 原配置中心是否已下发动态关闭
     *
     * @return 是否关闭
     */
    private boolean isDynamicClosed() {
        final Object config = ConfigHolder.INSTANCE.getConfig(OriginConfigDisableSource.ZK_CONFIG_CENTER_ENABLED);
        if (config == null) {
            return false;
        }
        return !Boolean.parseBoolean(config.toString());
    }
}

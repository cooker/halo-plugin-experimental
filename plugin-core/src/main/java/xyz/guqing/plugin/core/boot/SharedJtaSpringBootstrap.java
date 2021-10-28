/*
 * Copyright (C) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.guqing.plugin.core.boot;

import javax.sql.DataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import xyz.guqing.plugin.core.SpringBootPlugin;

/**
 * Introduce central Jta transaction management between app and plugins, which
 * connect to very different databases with different {@link DataSource}.
 *
 * <b>Note that related AutoConfigurations have to be excluded explicitly to avoid
 * duplicated resource retaining.</b>
 *
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class SharedJtaSpringBootstrap extends SpringBootstrap {

    public SharedJtaSpringBootstrap(SpringBootPlugin plugin,
                                    Class<?>... primarySources) {
        super(plugin, primarySources);
    }

    @Override
    protected String[] getExcludeConfigurations() {
        return ArrayUtils.addAll(super.getExcludeConfigurations(),
                "org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration");
    }

    @Override
    public ConfigurableApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext =
                (AnnotationConfigApplicationContext) super.createApplicationContext();
        // share dataSource
        importBeanFromMainContext(applicationContext, "xaDataSourceWrapper");
        importBeanFromMainContext(applicationContext, "transactionManager");
        return applicationContext;
    }

}

package com.jd.laf.ignite.spring.logger;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.logger.log4j2.Log4J2Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

@Configuration
@ConditionalOnClass(Configurator.class)
@Conditional(Log4j2Configuration.Log4j2Condition.class)
public class Log4j2Configuration {

    @Value("ignite.logger.config:log4j2.xml")
    private String config;

    @Bean
    public Log4J2Logger log4jListener() throws IgniteCheckedException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(config);
        if (url == null) {
            url = this.getClass().getClassLoader().getResource(config);
        }
        return url != null ? new Log4J2Logger(url) : new Log4J2Logger(config);
    }

    static class Log4j2Condition extends AnyNestedCondition {

        public Log4j2Condition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "ignite.logger", name = "type", havingValue = "log4j2")
        static class Log4j2PropertyCondition {
        }

        @ConditionalOnProperty(prefix = "ignite.logger", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                LogAutoConfiguration.LOG4J_CLASS,
                LogAutoConfiguration.SLF4J_CLASS
        })
        static class OnlyLog4j2OnClasspathCondition {
        }
    }
}

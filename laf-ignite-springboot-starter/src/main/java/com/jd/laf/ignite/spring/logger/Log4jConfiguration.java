package com.jd.laf.ignite.spring.logger;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.logger.log4j.Log4JLogger;
import org.apache.log4j.Logger;
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
@ConditionalOnClass(Logger.class)
@Conditional(Log4jConfiguration.Log4jCondition.class)
public class Log4jConfiguration {

    @Value("ignite.logger.config:log4j.xml")
    private String config;

    @Bean
    public Log4JLogger log4jListener() throws IgniteCheckedException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(config);
        if (url == null) {
            url = this.getClass().getClassLoader().getResource(config);
        }
        return url != null ? new Log4JLogger(url) : new Log4JLogger(config);
    }

    static class Log4jCondition extends AnyNestedCondition {

        public Log4jCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "ignite.logger", name = "type", havingValue = "log4j")
        static class Log4jPropertyCondition {
        }

        @ConditionalOnProperty(prefix = "ignite.logger", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                LogAutoConfiguration.LOG4J2_CLASS,
                LogAutoConfiguration.SLF4J_CLASS
        })
        static class OnlyLog4jOnClasspathCondition {
        }
    }
}

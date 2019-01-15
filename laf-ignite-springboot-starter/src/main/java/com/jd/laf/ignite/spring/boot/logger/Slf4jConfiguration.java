package com.jd.laf.ignite.spring.boot.logger;

import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Logger.class)
@Conditional(Slf4jConfiguration.Slf4jCondition.class)
public class Slf4jConfiguration {

    @Bean
    public Slf4jLogger slf4jLogger() {
        return new Slf4jLogger();
    }


    static class Slf4jCondition extends AnyNestedCondition {

        public Slf4jCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "ignite.logger", name = "type", havingValue = "slf4j")
        static class Slf4jPropertyCondition {
        }

        @ConditionalOnProperty(prefix = "ignite.logger", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                LogAutoConfiguration.LOG4J2_CLASS,
                LogAutoConfiguration.LOG4J_CLASS
        })
        static class OnlySlf4jOnClasspathCondition {
        }
    }
}

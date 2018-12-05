package com.jd.laf.ignite.spring.logger;

import org.apache.ignite.IgniteLogger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by bjliuyong on 2018/11/12.
 */
@Configuration
@ConditionalOnMissingBean(IgniteLogger.class)
@Import({
        Log4j2Configuration.class,
        Log4jConfiguration.class,
        Slf4jConfiguration.class
})
public class LogAutoConfiguration {

    public static final String LOG4J_CLASS = "org.apache.ignite.logger.log4j.Log4JLogger";
    public static final String LOG4J2_CLASS = "org.apache.ignite.logger.log4j2.Log4J2Logger";
    public static final String SLF4J_CLASS = "org.apache.ignite.logger.slf4j.Slf4jLogger";
}

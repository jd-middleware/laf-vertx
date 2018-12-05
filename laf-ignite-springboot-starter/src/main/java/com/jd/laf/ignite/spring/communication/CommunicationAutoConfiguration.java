package com.jd.laf.ignite.spring.communication;

import org.apache.ignite.spi.communication.CommunicationSpi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@ConditionalOnMissingBean(CommunicationSpi.class)
@Import({
        TcpCommunicationAutoConfiguration.class
})
public class CommunicationAutoConfiguration {

}

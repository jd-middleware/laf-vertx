package org.unbrokendome.vertx.spring;

import org.apache.ignite.configuration.IgniteConfiguration;

/**
 * Created by yangyang36 on 2018/10/26.
 */
public class IgniteConfigurationExt extends IgniteConfiguration {

    public IgniteConfigurationExt(){
        this.setLocalHost(NetUtils.getLocalHost());
    }


}

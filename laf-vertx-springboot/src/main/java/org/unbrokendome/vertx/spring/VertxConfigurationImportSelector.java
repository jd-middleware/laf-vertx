package org.unbrokendome.vertx.spring;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * 动态导入配置类
 */
public class VertxConfigurationImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(final AnnotationMetadata importingClassMetadata) {
        //获取是否部署Verticle标识
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableVertx.class.getName());
        boolean deployVerticles = (boolean) attributes.getOrDefault("deployVerticles", Boolean.TRUE);

        if (deployVerticles) {
            //自动部署Verticle
            return new String[]{VertxConfiguration.class.getName(), VerticleDeploymentConfiguration.class.getName()};
        } else {
            return new String[]{VertxConfiguration.class.getName()};
        }
    }
}

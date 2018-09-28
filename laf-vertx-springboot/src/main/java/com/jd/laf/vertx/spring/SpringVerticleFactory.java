package com.jd.laf.vertx.spring;

import io.vertx.core.Verticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.VerticleFactory;
import org.springframework.beans.factory.BeanFactory;

/**
 * Spring执行器工厂类
 */
public class SpringVerticleFactory implements VerticleFactory {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String prefix;
    private final BeanFactory beanFactory;


    public SpringVerticleFactory(String prefix, BeanFactory beanFactory) {
        this.prefix = prefix;
        this.beanFactory = beanFactory;
    }


    @Override
    public String prefix() {
        return prefix;
    }


    @Override
    public Verticle createVerticle(final String verticleName, final ClassLoader classLoader) throws Exception {
        return createVerticleFromBean(getBeanNameFromVerticleName(verticleName));
    }

    /**
     * 获取执行器的Bean名称
     *
     * @param verticleName verticle名称
     * @return
     */
    protected String getBeanNameFromVerticleName(final String verticleName) {
        return verticleName.startsWith(prefix + ":") ? verticleName.substring(prefix.length() + 1) : verticleName;
    }

    /**
     * 构建执行器
     *
     * @param beanName
     * @return
     */
    protected Verticle createVerticleFromBean(final String beanName) {
        if (!beanFactory.containsBean(beanName)) {
            throw new IllegalArgumentException("No such bean: " + beanName);
        } else if (!beanFactory.isTypeMatch(beanName, Verticle.class)) {
            throw new IllegalArgumentException("Bean \"" + beanName + "\" is not of type Verticle");
        } else if (beanFactory.isSingleton(beanName)) {
            throw new IllegalArgumentException("Verticle bean \"" + beanName + "\" is a singleton bean");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Creating verticle from bean \"{}\"", beanName);
        }
        return beanFactory.getBean(beanName, Verticle.class);
    }


    @Override
    public String toString() {
        return "SpringVerticleFactory with prefix \"" + prefix + "\"";
    }
}

package org.unbrokendome.vertx.spring;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.MethodMetadata;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Bean初始化后执行器处理器
 */
public class VerticleBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, BeanClassLoaderAware {

    private final Logger logger = LoggerFactory.getLogger(VerticleBeanPostProcessor.class);

    protected ClassLoader beanClassLoader;


    @Override
    public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (!isVerticleBeanDefinition(beanName, beanDefinition)) {
                continue;
            }
            AnnotatedTypeMetadata metadata = getMetadataFromBeanDefinition(beanDefinition);

            DeploymentOptions deploymentOptions = null;
            if (metadata != null && metadata.isAnnotated(VerticleDeployment.class.getName())) {
                Map<String, Object> attributes = metadata.getAnnotationAttributes(VerticleDeployment.class.getName());
                VerticleDeploymentAnnotationMirror annotationMirror = new VerticleDeploymentAnnotationMirror(attributes);
                if (!annotationMirror.isAutoDeploy()) {
                    continue;
                }
                deploymentOptions = new DeploymentOptions();
                annotationMirror.configure(deploymentOptions);
            }

            Integer order = null;
            if (metadata != null && metadata.isAnnotated(Order.class.getName())) {
                Map<String, Object> attributes = metadata.getAnnotationAttributes(Order.class.getName());
                order = (Integer) attributes.get("value");
            }

            BeanDefinition registrationBeanDefinition = new GenericBeanDefinition();
            registrationBeanDefinition.setBeanClassName(VerticleRegistrationBean.class.getName());
            MutablePropertyValues propertyValues = registrationBeanDefinition.getPropertyValues();

            ConfigurableBeanFactory beanFactory = null;
            if (registry instanceof ConfigurableBeanFactory) {
                beanFactory = (ConfigurableBeanFactory) registry;
            }
            boolean isFactoryBean = beanFactory != null && beanFactory.isFactoryBean(beanName);

            if (beanDefinition.isPrototype() || isFactoryBean) {
                propertyValues.add("verticleName", beanName);

            } else if (beanDefinition.isSingleton()) {
                //propertyValues.add("verticle", new RuntimeBeanReference(beanName));
                if (deploymentOptions != null && deploymentOptions.getInstances() > 1) {
                    logger.warn("A singleton verticle bean " + beanName + " was annotated with an instance count > 1, which will " +
                            "be ignored. To deploy multiple instances of this verticle, declare it as a prototype or " +
                            "factory bean.");
                    deploymentOptions.setInstances(1);
                }

            } else {
                logger.warn("A verticle bean must either be singleton, prototype or a factory bean");
                continue;
            }

            if (deploymentOptions != null) {
                propertyValues.add("deploymentOptions", deploymentOptions);
            }

            //propertyValues.add("name", beanName);
            if (order != null) {
                propertyValues.add("order", order);
            }
            registry.registerBeanDefinition(beanName + "#registration", registrationBeanDefinition);
        }
    }

    /**
     * 判断bean是否是Verticle
     *
     * @param beanName       bean名称
     * @param beanDefinition bean定义
     * @return
     */
    protected boolean isVerticleBeanDefinition(final String beanName, final BeanDefinition beanDefinition) {
        Class<?> beanClass = getBeanClass(beanName, beanDefinition);
        if (beanClass != null) {
            if (Verticle.class.isAssignableFrom(beanClass)) {
                return true;
            }
            //工厂Bean
            if (FactoryBean.class.isAssignableFrom(beanClass) && beanDefinition instanceof AnnotatedBeanDefinition) {
                MethodMetadata factoryMethodMetadata = ((AnnotatedBeanDefinition) beanDefinition).getFactoryMethodMetadata();
                if (factoryMethodMetadata != null && factoryMethodMetadata.isAnnotated(VerticleDeployment.class.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取bean的类型
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    protected Class<?> getBeanClass(final String beanName, final BeanDefinition beanDefinition) {
        if ((beanDefinition instanceof AbstractBeanDefinition)
                && ((AbstractBeanDefinition) beanDefinition).hasBeanClass()) {
            return ((AbstractBeanDefinition) beanDefinition).getBeanClass();
        }
        if (beanDefinition.getBeanClassName() != null) {
            try {
                return beanClassLoader.loadClass(beanDefinition.getBeanClassName());
            } catch (ClassNotFoundException ex) {
                logger.warn(String.format("Could not load class %s for bean %s", beanDefinition.getBeanClassName(), beanName), ex);
            }
        }
        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            MethodMetadata factoryMethodMetadata = ((AnnotatedBeanDefinition) beanDefinition).getFactoryMethodMetadata();
            if (factoryMethodMetadata != null && factoryMethodMetadata.getReturnTypeName() != null) {
                try {
                    return beanClassLoader.loadClass(factoryMethodMetadata.getReturnTypeName());
                } catch (ClassNotFoundException ex) {
                    logger.warn(String.format("Could not load class %s for bean %s", beanDefinition.getBeanClassName(), beanName), ex);
                }
            }
        }
        return null;
    }


    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    /**
     * 获取注解信息
     *
     * @param beanDefinition
     * @return
     */
    protected AnnotatedTypeMetadata getMetadataFromBeanDefinition(BeanDefinition beanDefinition) {
        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) beanDefinition;
            MethodMetadata factoryMethodMetadata = abd.getFactoryMethodMetadata();
            if (factoryMethodMetadata != null) {
                return factoryMethodMetadata;
            } else {
                return abd.getMetadata();
            }
        }
        return null;
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    /**
     * 执行器部署注解镜像
     */
    protected static class VerticleDeploymentAnnotationMirror {
        //注解属性
        protected final Map<String, Object> attributes;

        VerticleDeploymentAnnotationMirror(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        public boolean isAutoDeploy() {
            Boolean autoDeploy = (Boolean) attributes.get("autoDeploy");
            if (autoDeploy != null) {
                return autoDeploy;
            }
            autoDeploy = (Boolean) attributes.get("value");
            if (autoDeploy != null) {
                return autoDeploy;
            }
            return true;
        }

        public void configure(final DeploymentOptions options) {
            withAnnotationAttribute("worker", options::setWorker);
            withAnnotationAttribute("multiThreaded", options::setMultiThreaded);
            withAnnotationAttribute("ha", options::setHa);
            withAnnotationAttribute("instances", options::setInstances);
            withAnnotationAttribute("workerPoolName", options::setWorkerPoolName);
            withAnnotationAttribute("workerPoolSize", options::setWorkerPoolSize);
            withAnnotationAttribute("maxWorkerExecuteTime", options::setMaxWorkerExecuteTime);
        }

        @SuppressWarnings("unchecked")
        protected <T> void withAnnotationAttribute(final String key, final Consumer<T> consumer) {
            T value = (T) attributes.get(key);
            if (value != null) {
                consumer.accept(value);
            }
        }
    }
}

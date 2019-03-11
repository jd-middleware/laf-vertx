package com.jd.laf.web.vertx.binding;


import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.binder.Binder.Context;
import com.jd.laf.binding.reflect.FieldAccessorFactory;
import com.jd.laf.binding.reflect.exception.ReflectionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.jd.laf.binding.Plugin.BINDER;

/**
 * 方法参数绑定器
 */
public class ParamBinding {

    protected static ConcurrentMap<String, List<BindingMethod>> bindingMethods = new ConcurrentHashMap();

    /**
     * 绑定上下文
     *
     * @param source  上下文
     * @param target  对象
     * @param method 执行方法
     * @param name  处理器
     * @throws ReflectionException
     */
    public static void bind(final Object source, final Object target,final Method method, String name) throws ReflectionException {
        if (source == null || target == null) {
            return;
        }
        // 从缓存中获取
        List<BindingMethod> bindings = bindingMethods.get(name);
        if (bindings == null) {
            // 没有找到则从注解中查找
            bindings = new ArrayList<>();
            Annotation[][] annotations;
            BindingMethod bindingMethod;
            Binder binder;
            annotations = method.getParameterAnnotations();
            for (int i = 0; i<annotations.length; i++) {
                bindingMethod = null;
                //是否是绑定注解
                Annotation annotation = annotations[i][0];
                binder = BINDER.get(annotation.annotationType());
                if (binder != null) {
                    if (bindingMethod == null) {
                        bindingMethod = new BindingMethod(method, i, target);
                    }
                    bindingMethod.add(new BinderAnnotation(annotation, binder));
                }
                //有绑定注解
                if (bindingMethod != null) {
                    bindings.add(bindingMethod);
                }
            }
            target.getClass().getName();
            List<BindingMethod> exists = bindingMethods.putIfAbsent(name, bindings);
            if (exists != null) {
                bindings = exists;
            }
        }
        for (BindingMethod binding : bindings) {
            binding.bind(source, binding, null);
        }
    }
    /**
     * 绑定字段
     */
    public static class BindingMethod{
        //字段
        final protected Method method;
        final int paramIndex;
        final protected Object target;
        //绑定实现
        final protected List<BinderAnnotation> annotations = new ArrayList<>(2);

        public BindingMethod(Method method, int paramIndex, Object target) {
            this.method = method;
            this.paramIndex = paramIndex;
            this.target = target;
        }

        public Method getMethod() {
            return method;
        }

        public int getParamIndex() {
            return paramIndex;
        }

        public Object getTarget() {
            return target;
        }

        public List<BinderAnnotation> getAnnotations() {
            return annotations;
        }

        public void add(final BinderAnnotation annotation) {
            if (annotation != null) {
                annotations.add(annotation);
            }
        }

        /**
         * 绑定
         *
         * @param source
         * @param target
         * @param factory
         * @throws ReflectionException
         */
        public void bind(final Object source, final Object target, final FieldAccessorFactory factory) throws ReflectionException {
            Context context;
            for (BinderAnnotation annotation : annotations) {
                context = new Context(target, null, annotation.annotation, factory, source);
                if (annotation.binder.bind(context)) {
                    return;
                }
            }

        }
    }
    /**
     * 注解绑定器
     */
    protected static class BinderAnnotation {
        final protected Annotation annotation;
        final protected Binder binder;

        public BinderAnnotation(Annotation annotation, Binder binder) {
            this.annotation = annotation;
            this.binder = binder;
        }

        public Annotation getAnnotation() {
            return annotation;
        }

        public Binder getBinder() {
            return binder;
        }
    }

}

package com.jd.laf.web.vertx.binder;

import com.jd.laf.binding.binder.Binder;
import com.jd.laf.binding.reflect.exception.ReflectionException;
import com.jd.laf.web.vertx.annotation.CPath;
import io.vertx.ext.web.RoutingContext;

/**
 * 方法路径
 */
public class PathBinder implements Binder {

    @Override
    public boolean bind(Context context) throws ReflectionException {
        if (context == null) {
            return false;
        }
        Object obj = context.getSource();
        if (!(obj instanceof RoutingContext)) {
            return false;
        }
        RoutingContext ctx = (RoutingContext) obj;
        return context.bind(ctx.normalisedPath());
    }

    @Override
    public Class<?> annotation() {
        return CPath.class;
    }

}

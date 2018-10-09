package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Command;
import com.jd.laf.web.vertx.ErrorHandler;
import com.jd.laf.web.vertx.render.Render;
import com.jd.laf.web.vertx.response.Response;
import com.jd.laf.web.vertx.response.Responses;
import io.vertx.ext.web.RoutingContext;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jd.laf.web.vertx.render.Render.APPLICATION_JSON;
import static com.jd.laf.web.vertx.render.Renders.getPlugin;

/**
 * 异常处理类
 */
public class ExceptionHandler implements ErrorHandler {

    protected static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

    @Override
    public String type() {
        return "error";
    }


    @Override
    public void handle(final RoutingContext context) {
        Throwable throwable = context.failure();
        logger.log(Level.SEVERE, context.request().path() + ": " + throwable.getMessage(), throwable);
        Response response = Responses.error(throwable);
        try {
            context.put(Command.RESULT, response);
            String contentType = context.getAcceptableContentType();
            contentType = contentType == null ? APPLICATION_JSON : contentType.toLowerCase();
            Render render = getPlugin(contentType);
            render = render == null ? RenderHandler.JSON : render;
            render.render(context);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

    }
}

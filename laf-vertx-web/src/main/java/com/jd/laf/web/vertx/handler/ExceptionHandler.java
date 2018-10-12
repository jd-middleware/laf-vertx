package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Command;
import com.jd.laf.web.vertx.ErrorHandler;
import com.jd.laf.web.vertx.render.Render;
import com.jd.laf.web.vertx.response.ErrorResponse;
import com.jd.laf.web.vertx.response.Response;
import com.jd.laf.web.vertx.response.Responses;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.render.Render.APPLICATION_JSON;
import static com.jd.laf.web.vertx.render.Renders.getPlugin;

/**
 * 异常处理类
 */
public class ExceptionHandler implements ErrorHandler {

    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public String type() {
        return "error";
    }


    @Override
    public void handle(final RoutingContext context) {
        //异常
        Throwable throwable = context.failure();
        //异常响应
        Response response = Responses.error(throwable);
        if (response != null && response instanceof ErrorResponse && !((ErrorResponse) response).isTrace()) {
            //业务异常，不需要打印详细堆栈
            logger.error(context.request().path() + ": " + throwable.getMessage());
        } else {
            logger.error(context.request().path() + ": " + throwable.getMessage(), throwable);
        }
        try {
            context.put(Command.RESULT, response);
            if (!context.response().ended()) {
                String contentType = context.getAcceptableContentType();
                contentType = contentType == null ? APPLICATION_JSON : contentType.toLowerCase();
                Render render = getPlugin(contentType);
                render = render == null ? RenderHandler.JSON : render;
                render.render(context);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }
}

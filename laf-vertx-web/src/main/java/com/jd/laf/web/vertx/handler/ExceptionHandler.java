package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Command;
import com.jd.laf.web.vertx.ErrorHandler;
import com.jd.laf.web.vertx.response.ErrorResponse;
import com.jd.laf.web.vertx.response.Response;
import com.jd.laf.web.vertx.response.Responses;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import static com.jd.laf.web.vertx.handler.RenderHandler.render;

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
        context.put(Command.RESULT, response);

        //输出日志
        if (response != null) {
            if (throwable != null && response instanceof ErrorResponse && ((ErrorResponse) response).isTrace()) {
                logger.error(context.request().path() + ": " + response.getMessage(), throwable);
            } else {
                logger.error(context.request().path() + ": " + response.getMessage());
            }
        } else if (throwable != null) {
            logger.error(context.request().path() + ": " + throwable.getMessage(), throwable);
        } else {
            logger.error(context.request().path() + ": " + "unknown error.");
        }

        //渲染输出
        render(context, false);

    }
}

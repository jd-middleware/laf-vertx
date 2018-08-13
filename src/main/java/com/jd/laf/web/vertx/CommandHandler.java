package com.jd.laf.web.vertx;

import com.jd.laf.binding.Binding;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * 命令处理
 */
public class CommandHandler implements Handler<RoutingContext> {

    protected Command command;

    public CommandHandler(Command command) {
        this.command = command;
    }

    @Override
    public void handle(final RoutingContext context) {
        try {
            //克隆一份
            Command clone = command.getClass().newInstance();
            //绑定
            Binding.bind(context, clone);
            //验证
            validate(clone, context.get(Command.VALIDATOR));
            //执行
            Command.Result result = clone.execute();
            if (result != null) {
                //有返回结果
                if (result.getKey() != null) {
                    context.put(result.getKey(), result.getResult());
                }
                //继续执行
                if (result.getType() == Command.ResultType.CONTINUE) {
                    context.next();
                }
            } else {
                context.next();
            }
        } catch (Exception e) {
            context.fail(e);
        }
    }

    /**
     * 验证
     *
     * @param command
     * @param validator
     */
    protected void validate(final Command command, final Validator validator) {
        if (command == null || validator == null) {
            return;
        }
        Set<ConstraintViolation<Command>> constraints = validator.validate(command);
        if (constraints != null && !constraints.isEmpty()) {
            ConstraintViolation<Command> violation = constraints.iterator().next();
            throw new ValidationException(violation.getMessage());
        }
    }
}

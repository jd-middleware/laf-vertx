package com.jd.laf.web.vertx;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * 工具类
 */
public abstract class Validates {

    //静态的验证器
    protected static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    /**
     * 验证
     *
     * @param target 目标对象
     */
    public static void validate(final Object target) {
        validate(validator, target);
    }


    /**
     * 验证
     *
     * @param validator 验证器
     * @param target    目标对象
     */
    public static void validate(final Validator validator, final Object target) {
        if (validator == null || target == null) {
            return;
        }
        Set<ConstraintViolation<Object>> constraints = validator.validate(target);
        if (constraints != null && !constraints.isEmpty()) {
            StringBuilder builder = new StringBuilder(100);
            int count = 0;
            for (ConstraintViolation<Object> violation : constraints) {
                if (count++ > 0) {
                    builder.append('\n');
                }
                builder.append(violation.getMessage());
            }
            throw new ValidationException(builder.toString());
        }
    }
}

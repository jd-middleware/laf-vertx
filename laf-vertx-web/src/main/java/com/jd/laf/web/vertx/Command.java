package com.jd.laf.web.vertx;

import com.jd.laf.extension.Prototype;
import com.jd.laf.extension.Type;

/**
 * 命令接口,非单例
 */
public interface Command<T> extends Type<String>, Cloneable, Prototype {

    /**
     * 结果
     */
    String RESULT = "result";

    /**
     * 执行命令
     *
     * @return
     * @throws Exception
     */
    default T execute() throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * 名称
     *
     * @return
     */
    @Override
    default String type() {
        String name = this.getClass().getSimpleName();
        int length = name.length();
        if (name.endsWith("Command")) {
            length -= 7;
        }
        char[] chars = new char[length];
        name.getChars(0, length, chars, 0);
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    /**
     * 返回结果
     */
    class Result {
        //类型
        ResultType type;
        //返回值
        Object result;
        //返回值存放的键
        String key;
        //渲染模板
        String template;

        public Result(Object result) {
            this(ResultType.CONTINUE, result, null, null);
        }

        public Result(Object result, String template) {
            this(ResultType.CONTINUE, result, null, template);
        }

        public Result(ResultType type, Object result) {
            this(type, result, null, null);
        }

        public Result(ResultType type, Object result, String key) {
            this(type, result, key, null);
        }

        public Result(ResultType type, Object result, String key, String template) {
            this.type = type == null ? ResultType.CONTINUE : type;
            this.result = result;
            this.key = key == null && result != null ? RESULT : key;
            this.template = template;
        }

        public ResultType getType() {
            return type;
        }

        public Object getResult() {
            return result;
        }

        public String getKey() {
            return key;
        }

        public String getTemplate() {
            return template;
        }
    }

    /**
     * 没有返回值，继续
     */
    class EmptyContinue extends Result {
        public EmptyContinue() {
            super(ResultType.CONTINUE, null, null);
        }
    }

    /**
     * 没有返回值，挂住
     */
    class EmptyHold extends Result {
        public EmptyHold() {
            super(ResultType.HOLD, null, null);
        }
    }

    /**
     * 返回类型
     */
    enum ResultType {
        /**
         * 继续链执行
         */
        CONTINUE,
        /**
         * 渲染输出
         */
        END,
        /**
         * 挂住
         */
        HOLD
    }
}

package com.jd.laf.web.vertx;

/**
 * 命令接口
 */
public interface Command {

    /**
     * 命令的结果
     */
    String RESULT = "command.result";
    /**
     * 验证器
     */
    String VALIDATOR = "command.validator";

    /**
     * 执行命令
     *
     * @return
     * @throws Exception
     */
    Result execute() throws Exception;

    /**
     * 名称
     *
     * @return
     */
    String type();

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

        public Result(Object result) {
            this(ResultType.CONTINUE, result, null);
        }

        public Result(ResultType type, Object result) {
            this(type, result, null);
        }

        public Result(ResultType type, Object result, String key) {
            this.type = type == null ? ResultType.CONTINUE : type;
            this.result = result;
            this.key = key == null && result != null ? RESULT : key;
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

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
            this(ResultType.CONTINUE, result, RESULT);
        }

        public Result(ResultType type, Object result) {
            this(type, result, RESULT);
        }

        public Result(ResultType type, Object result, String key) {
            this.type = type;
            this.result = result;
            this.key = key;
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
     * 返回类型
     */
    enum ResultType {
        /**
         * 继续链执行
         */
        CONTINUE,
        /**
         * 挂住
         */
        HOLD
    }
}

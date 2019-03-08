package com.jd.laf.web.vertx.handler;

/**
 * 处理器 工具类
 */
public class Handlers {

    public static final String METHOD_HANDLER_DELIMITER = ".";

    public static boolean isMethodHandler(String name) {
        if (name.contains(METHOD_HANDLER_DELIMITER)) {
            return true;
        }
        return false;
    }

    public static String getCommand(String name) {
        if (isMethodHandler(name)) {
            return name.split("\\" + METHOD_HANDLER_DELIMITER)[0];
        }
        return name;
    }

    public static String getPath(String name) {
        if (isMethodHandler(name)) {
            return name.split("\\" + METHOD_HANDLER_DELIMITER)[1];
        }
        return null;
    }

    public static String getMethodHandler(String command, String path) {
        return command + METHOD_HANDLER_DELIMITER + path;
    }
}

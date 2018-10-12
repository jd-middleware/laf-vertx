package com.jd.laf.web.vertx.response;

/**
 * 异常响应
 */
public class ErrorResponse extends Response {
    //是否打印异常栈
    protected transient boolean trace = true;

    public ErrorResponse(int code, String message) {
        super(code, message);
    }

    public ErrorResponse(int code, int status, String message) {
        super(code, status, message);
    }

    public ErrorResponse(int code, String message, boolean trace) {
        super(code, message);
        this.trace = trace;
    }

    public ErrorResponse(int code, int status, String message, boolean trace) {
        super(code, status, message);
        this.trace = trace;
    }

    public boolean isTrace() {
        return trace;
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ErrorResponse{");
        sb.append("code=").append(code);
        sb.append(", status=").append(status);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

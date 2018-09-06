package com.jd.laf.web.vertx.response;

/**
 * 响应
 */
public class Response {
    public static final int OK = 200;
    public static final int INTERNAL_ERROR = 500;
    //响应码
    protected int code;
    //HTTP响应码
    protected transient int status;
    //信息
    protected String message;
    //数据
    protected Object data;

    public Response() {
        this(OK, OK, null, null);
    }

    public Response(int code) {
        this(code, OK, null, null);
    }

    public Response(Object data) {
        this(OK, OK, null, data);
    }

    public Response(int code, String message) {
        this(code, OK, message, null);
    }

    public Response(int code, Object data) {
        this(code, OK, null, data);
    }

    public Response(int code, String message, Object data) {
        this(code, OK, message, data);
    }

    public Response(int code, int status, String message, Object data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

}

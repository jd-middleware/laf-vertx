package com.jd.laf.web.vertx.command;

import com.jd.laf.web.vertx.Command;

public class HelloWordCommand implements Command {

    @Override
    public Result execute() throws Exception {
        return new Result("helloword!");
    }

    @Override
    public String type() {
        return "helloWorld";
    }
}

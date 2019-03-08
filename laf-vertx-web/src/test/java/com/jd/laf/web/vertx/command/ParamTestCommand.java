package com.jd.laf.web.vertx.command;

import com.jd.laf.web.vertx.Command;
import com.jd.laf.web.vertx.annotation.parameter.Body;
import com.jd.laf.web.vertx.annotation.parameter.QueryParam;
import com.jd.laf.web.vertx.annotation.method.Path;
import com.jd.laf.web.vertx.pool.Poolable;

public class ParamTestCommand implements Command, Poolable {

    @Override
    public Object execute() throws Exception {
        return null;
    }

    @Path("test")
    //, @Body(paramIndex = 0, type = Body.BodyType.TEXT) Object name
    public Result test(@QueryParam("id") Object id, @QueryParam("code") Object code) throws Exception {
        return new Result(id + "_" + code);
    }

    @Path("test2")
    //, @Body(paramIndex = 0, type = Body.BodyType.TEXT) Object name
    public Result test2(@Body(paramIndex = 0, type = Body.BodyType.JSON) Object mytest) throws Exception {
        return new Result(mytest.toString());
    }

    @Override
    public void clean() {

    }
}

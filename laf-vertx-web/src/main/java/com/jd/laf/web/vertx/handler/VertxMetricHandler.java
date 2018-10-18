package com.jd.laf.web.vertx.handler;

import com.jd.laf.web.vertx.Environment;
import com.jd.laf.web.vertx.EnvironmentAware;
import com.jd.laf.web.vertx.RoutingHandler;
import io.micrometer.core.instrument.*;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * 系统指标
 */
public class VertxMetricHandler implements RoutingHandler, EnvironmentAware {

    public final static String CONTENT_TYPE_004 = "text/plain; version=0.0.4; charset=utf-8";

    protected MeterRegistry registry;

    @Override
    public void setup(final Vertx vertx, final Environment environment) throws Exception {
        //尝试获取MeterRegistry
        String name = environment.getString("vertx.metric.registry.name");
        if (name != null && !name.isEmpty()) {
            registry = environment.getObject(name, MeterRegistry.class);
        } else {
            registry = environment.getObject("prometheusMeterRegistry", MeterRegistry.class);
            if (registry == null) {
                registry = environment.getObject("influxMeterRegistry", MeterRegistry.class);
            }
            if (registry == null) {
                registry = environment.getObject("jmxMeterRegistry", MeterRegistry.class);
            }
        }
    }

    @Override
    public String type() {
        return "vertxMetric";
    }

    @Override
    public void handle(final RoutingContext context) {
        if (registry != null) {
            StringWriter writer = new StringWriter();
            try {
                for (Meter meter : registry.getMeters()) {
                    write004(writer, meter);
                }
                context.response().putHeader("Content-Type", CONTENT_TYPE_004).end(writer.toString());
            } catch (IOException e) {
                context.response().setStatusCode(500).putHeader("Content-Type", CONTENT_TYPE_004).end(e.getMessage());
            }

        } else {
            context.response().setStatusCode(500).putHeader("Content-Type", CONTENT_TYPE_004).end("MeterRegistry is not found.");
        }

    }

    protected static void write004(final Writer writer, final Meter meter) throws IOException {
        Meter.Id id = meter.getId();
        writer.write("# HELP ");
        writer.write(id.getName());
        writer.write(' ');
        writeEscapedHelp(writer, id.getDescription());
        writer.write('\n');
        writer.write("# TYPE ");
        writer.write(id.getName());
        writer.write(' ');
        switch (id.getType()) {
            case GAUGE:
                writer.write("gauge");
                break;
            case COUNTER:
                writer.write("counter");
                break;
            case DISTRIBUTION_SUMMARY:
                writer.write(((DistributionSummary) meter).count() > 0 ? "histogram" : "summary");
                break;
            case TIMER:
                writer.write(((io.micrometer.core.instrument.Timer) meter).count() > 0 ? "histogram" : "summary");
                break;
            default:
                writer.write("untyped");
                break;
        }
        writer.write('\n');
        List<Tag> tags;
        int i;
        for (Measurement measurement : meter.measure()) {
            writer.write(id.getName());
            tags = id.getTags();
            if (tags != null && !tags.isEmpty()) {
                i = 0;
                writer.write('{');
                for (Tag tag : tags) {
                    if (i++ > 0) {
                        writer.write(',');
                    }
                    writer.write(tag.getKey());
                    writer.write("=\"");
                    writeEscapedLabelValue(writer, tag.getValue());
                    writer.write("\"");
                }
                writer.write('}');
            }
            writer.write(' ');
            writer.write(doubleToGoString(measurement.getValue()));
            writer.write('\n');
        }
    }

    protected static void writeEscapedHelp(final Writer writer, final String s) throws IOException {
        if (s == null) {
            return;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\':
                    writer.append("\\\\");
                    break;
                case '\n':
                    writer.append("\\n");
                    break;
                default:
                    writer.append(c);
            }
        }
    }

    protected static void writeEscapedLabelValue(final Writer writer, final String s) throws IOException {
        if (s == null) {
            return;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\':
                    writer.append("\\\\");
                    break;
                case '\"':
                    writer.append("\\\"");
                    break;
                case '\n':
                    writer.append("\\n");
                    break;
                default:
                    writer.append(c);
            }
        }
    }

    protected static String doubleToGoString(final double d) {
        if (d == 1.0D / 0.0) {
            return "+Inf";
        } else if (d == -1.0D / 0.0) {
            return "-Inf";
        } else {
            return Double.isNaN(d) ? "NaN" : Double.toString(d);
        }
    }
}

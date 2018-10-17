package org.unbrokendome.vertx.spring.actuator.metrics;

public abstract class AbstractPartMetricsProperties {

    protected String prefix;
    protected boolean enabled = true;

    public AbstractPartMetricsProperties(String defaultPrefix) {
        this.prefix = defaultPrefix;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

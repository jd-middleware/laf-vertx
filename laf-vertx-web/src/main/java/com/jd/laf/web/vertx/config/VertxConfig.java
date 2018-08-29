package com.jd.laf.web.vertx.config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.jd.laf.web.vertx.config.RouteConfig.PLACE_HOLDER;

/**
 * 配置
 */
@XmlRootElement(name = "vertx")
@XmlAccessorType(XmlAccessType.NONE)
public class VertxConfig {
    //路由处理器
    @XmlElementWrapper
    @XmlElement(name = "route")
    List<RouteConfig> routes = new ArrayList<>(50);
    //消息处理器
    @XmlElementWrapper
    @XmlElement(name = "messages")
    List<RouteConfig> messages = new ArrayList<>(10);

    public VertxConfig() {
    }

    public List<RouteConfig> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteConfig> routes) {
        this.routes = routes;
    }

    public List<RouteConfig> getMessages() {
        return messages;
    }

    public void setMessages(List<RouteConfig> messages) {
        this.messages = messages;
    }

    /**
     * 添加路由处理器配制
     *
     * @param route 路由处理器配制
     */
    public void add(final RouteConfig route) {
        if (route != null) {
            if (RouteType.MSG == route.getType()) {
                messages.add(route);
            } else {
                routes.add(route);
            }
        }
    }

    /**
     * 构造器
     */
    public static class Builder {

        /**
         * 构造配置，没有处理继承
         *
         * @param reader
         * @return
         * @throws IOException
         */
        public static VertxConfig build(final Reader reader) throws JAXBException {
            JAXBContext context = JAXBContext.newInstance(RouteConfig.class, RouteType.class, VertxConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (VertxConfig) unmarshaller.unmarshal(reader);
        }

        /**
         * 构造配置，没有处理继承
         *
         * @throws IOException
         * @throws JAXBException
         */
        public static VertxConfig build(String file) throws IOException, JAXBException {
            if (file == null || file.isEmpty()) {
                throw new IllegalStateException("file can not be empty.");
            }
            InputStream in;
            BufferedReader reader = null;
            try {
                File f = new File(file);
                if (f.exists()) {
                    in = new FileInputStream(f);
                } else {
                    in = VertxConfig.class.getClassLoader().getResourceAsStream(file);
                    if (in == null) {
                        throw new IOException("file is not found. " + file);
                    }
                }
                reader = new BufferedReader(new InputStreamReader(in));
                return build(reader);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }

        /**
         * 处理继承
         *
         * @param config
         * @return
         */
        public static VertxConfig inherit(final VertxConfig config) {
            if (config == null) {
                return null;
            }
            List<RouteConfig> routes = config.getRoutes();
            if (routes == null || routes.isEmpty()) {
                return config;
            }
            LinkedList<RouteConfig> inherits = new LinkedList<>();
            for (RouteConfig cfg : config.getRoutes()) {
                if (cfg.getInherit() == null || cfg.getInherit().isEmpty() || cfg.isRoute()) {
                    continue;
                }
                inherits.add(cfg);
            }
            if (inherits == null || inherits.isEmpty()) {
                return config;
            }

            Map<String, RouteConfig> map = config.getRoutes().stream().filter(a -> a.getName() != null)
                    .collect(Collectors.toMap(a -> a.getName(), a -> a));
            RouteConfig parent;
            List<String> result;
            boolean flag;
            //当前节点遍历过的节点，防止递归
            Set<RouteConfig> graph = new HashSet<>(map.size());
            for (RouteConfig cfg : inherits) {
                //获取当前节点
                parent = map.get(cfg.getInherit());
                //清理当前节点遍历过的节点
                graph.clear();
                graph.add(cfg);
                while (parent != null && graph.add(parent)) {
                    if (cfg.getType() == null && parent.getType() != null) {
                        cfg.setType(parent.getType());
                    }
                    if ((cfg.getConsumes() == null || cfg.getConsumes().isEmpty())
                            && parent.getConsumes() != null && !parent.getConsumes().isEmpty()) {
                        cfg.setConsumes(parent.getConsumes());
                    }
                    if ((cfg.getProduces() == null || cfg.getProduces().isEmpty())
                            && parent.getProduces() != null && !parent.getProduces().isEmpty()) {
                        cfg.setProduces(parent.getProduces());
                    }
                    //处理业务处理器
                    if (parent.getHandlers() != null && !parent.getHandlers().isEmpty()) {
                        if (cfg.getHandlers() == null || cfg.getHandlers().isEmpty()) {
                            cfg.setHandlers(parent.getHandlers());
                        } else {
                            result = new ArrayList<>(parent.getHandlers().size()
                                    + (cfg.getHandlers() == null ? 0 : cfg.getHandlers().size()));
                            flag = false;
                            for (String b : parent.getHandlers()) {
                                if (!PLACE_HOLDER.equals(b)) {
                                    result.add(b);
                                } else if (!flag) {
                                    flag = true;
                                    result.addAll(cfg.getHandlers());
                                }
                            }
                            if (!flag) {
                                result.addAll(cfg.getHandlers());
                            }
                            cfg.setHandlers(result);
                        }
                    }
                    //处理异常处理器，直接覆盖
                    if ((cfg.getErrors() == null || cfg.getErrors().isEmpty())
                            && parent.getErrors() != null && !parent.getErrors().isEmpty()) {
                        cfg.setErrors(parent.getHandlers());
                    }
                    parent = parent.getInherit() == null || parent.getInherit().isEmpty() ? null : map.get(parent.getInherit());
                }
            }
            return config;
        }

    }

}

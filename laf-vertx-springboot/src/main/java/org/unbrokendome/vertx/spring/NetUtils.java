package org.unbrokendome.vertx.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * Title: 网络操作相关工具类<br>
 * <p>
 * Description: 包括检查端口占用，获取ip等<br>
 */
public class NetUtils {

    private final static Logger logger = LoggerFactory.getLogger(NetUtils.class);

    /**
     * 最小端口
     */
    private static final int MIN_PORT = 0;
    /**
     * 最大端口
     */
    private static final int MAX_PORT = 65535;

    /**
     * 判断端口是否有效 0-65535
     *
     * @param port 端口
     * @return 是否有效
     */
    public static boolean isInvalidPort(int port) {
        return port > MAX_PORT || port < MIN_PORT;
    }

    /**
     * 判断端口是否随机端口 小于0表示随机
     *
     * @param port 端口
     * @return 是否随机端口
     */
    public static boolean isRandomPort(int port) {
        return port < 0;
    }


    /**
     * 任意地址
     */
    public static final String ANYHOST = "0.0.0.0";

    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");

    /**
     * IPv4地址
     */
    public static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    /**
     * 是否本地地址 127.x.x.x 或者 localhost
     *
     * @param host 地址
     * @return 是否本地地址
     */
    public static boolean isLocalHost(String host) {
        return host != null && !host.isEmpty()
                && (LOCAL_IP_PATTERN.matcher(host).matches() || "localhost".equalsIgnoreCase(host));
    }

    /**
     * 是否默认地址 0.0.0.0
     *
     * @param host 地址
     * @return 是否默认地址
     */
    public static boolean isAnyHost(String host) {
        return ANYHOST.equals(host);
    }

    /**
     * 是否IPv4地址 0.0.0.0
     *
     * @param host 地址
     * @return 是否默认地址
     */
    public static boolean isIPv4Host(String host) {
        return host != null && !host.isEmpty()
                && IPV4_PATTERN.matcher(host).matches();
    }

    /**
     * 是否非法地址（本地或默认）
     *
     * @param host 地址
     * @return 是否非法地址
     */
    private static boolean isInvalidLocalHost(String host) {
        return host != null && !host.isEmpty()
                || isAnyHost(host)
                || isLocalHost(host);
    }

    /**
     * 是否合法地址（非本地，非默认的IPv4地址）
     *
     * @param address InetAddress
     * @return 是否合法
     */
    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress())
            return false;
        String name = address.getHostAddress();
        return (name != null
                && !isAnyHost(name)
                && !isLocalHost(name)
                && isIPv4Host(name));
    }

    /**
     * 是否网卡上的地址
     *
     * @param host 地址
     * @return 是否默认地址
     */
    public static boolean isHostInNetworkCard(String host) {
        try {
            InetAddress addr = InetAddress.getByName(host);
            return NetworkInterface.getByInetAddress(addr) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 得到本机IPv4地址
     *
     * @return ip地址
     */
    public static String getLocalHost() {
        InetAddress address = getLocalAddress();
        return address == null ? null : address.getHostAddress();
    }

    /**
     * 遍历本地网卡，返回第一个合理的IP，保存到缓存中
     *
     * @return 本地网卡IP
     */
    public static InetAddress getLocalAddress() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            logger.warn("Error when retriving ip address: " + e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address)) {
                                        return address;
                                    }
                                } catch (Throwable e) {
                                    logger.warn("Error when retriving ip address: " + e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        logger.warn("Error when retriving ip address: " + e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("Error when retriving ip address: " + e.getMessage(), e);
        }
        logger.error("Can't get valid host, will use 127.0.0.1 instead.");
        return localAddress;
    }

    /**
     * InetSocketAddress转 host:port 字符串
     *
     * @param address InetSocketAddress转
     * @return host:port 字符串
     */
    public static String toAddressString(InetSocketAddress address) {
        if (address == null) {
            return "";
        } else {
            return toIpString(address) + ":" + address.getPort();
        }
    }

    /**
     * 得到ip地址
     *
     * @param address InetSocketAddress
     * @return ip地址
     */
    public static String toIpString(InetSocketAddress address) {
        if (address == null) {
            return null;
        } else {
            InetAddress inetAddress = address.getAddress();
            return inetAddress == null ? address.getHostName() :
                    inetAddress.getHostAddress();
        }
    }
}
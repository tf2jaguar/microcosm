package io.github.tf2jaguar.micro.distribute.util;

import java.net.*;
import java.util.Enumeration;

/**
 * 获取当前机器名称、机器IP
 *
 * @author zhangguodong
 */
public class HostUtil {

    private static String HOST_NAME = "UnknownHost";
    private static String HOST_ADDRESS = "UnknownHostAddress";

    static {
        HOST_NAME = initHostName();
        HOST_ADDRESS = initLocalHostAddress();
    }

    /**
     * 获取host 名称
     *
     * @return jelly-mac
     */
    public static String getHostName() {
        return HOST_NAME;
    }

    /**
     * 获取host地址
     *
     * @return 192.168.101.11
     */
    public static String getHostAddress() {
        return HOST_ADDRESS;
    }

    private static String initHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HOST_NAME;
    }

    private static String initLocalHostAddress() {
        try {
            for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                NetworkInterface item = e.nextElement();
                for (InterfaceAddress address : item.getInterfaceAddresses()) {
                    if (item.isLoopback() || !item.isUp() || item.isPointToPoint()
                            || address.getAddress() instanceof Inet6Address) {
                        continue;
                    }
                    return address.getAddress().getHostAddress();
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        return HOST_ADDRESS;
    }

    /**
     * 是否符合本机优先， hostname 为空时返回 true，即默认当做本机处理
     *
     * @param hostname 机器名
     * @return 是否匹配
     */
    public static boolean matchLocalFirst(String hostname) {
        if (hostname == null || "".equals(hostname)) {
            return true;
        }
        return getHostName().equals(hostname);
    }


    /**
     * 将 ip 字符串转换为 int 类型的数字
     *
     * 思路就是将 ip 的每一段数字转为 8 位二进制数，并将它们放在结果的适当位置上
     *
     * @param ipString ip字符串，如 127.0.0.1
     * @return ip字符串对应的 int 值
     */
    public static int ip2Int(String ipString) {
        String[] ipSlices = ipString.split("\\.");
        if (ipSlices.length < 2) {
            return 0;
        }

        int rs = 0;
        for (int i = 0; i < ipSlices.length; i++) {
            // 将 ip 的每一段解析为 int，并根据位置左移 8 位
            int intSlice = Integer.parseInt(ipSlices[i]) << 8 * i;
            rs = rs | intSlice;
        }
        return rs;
    }

    /**
     * 将 int 转换为 ip 字符串
     *
     * @param ipInt 用 int 表示的 ip 值
     * @return ip字符串，如 127.0.0.1
     */
    public static String int2Ip(int ipInt) {
        String[] ipString = new String[4];
        for (int i = 0; i < 4; i++) {
            int pos = i * 8;
            int and = ipInt & (255 << pos);
            // 将当前 ip 段转换为 0 ~ 255 的数字，注意这里必须使用无符号右移
            ipString[i] = String.valueOf(and >>> pos);
        }
        return String.join(".", ipString);
    }
}
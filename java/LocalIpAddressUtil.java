package cn.byodwork.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by FrankieXiao on 2015/12/12.
 */
public class LocalIpAddressUtil {
    /**
        * 获取本地ip地址，有可能会有多个地址, 若有多个网卡则会搜集多个网卡的ip地址
    */
    private static Logger logger = LoggerFactory.getLogger(LocalIpAddressUtil.class);

    public static Set<InetAddress> resolveLocalAddresses() {
        Set<InetAddress> addrs = new HashSet<InetAddress>();
        Enumeration<NetworkInterface> ns = null;
        try {
            ns = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            // ignored...
        }
        while (ns != null && ns.hasMoreElements()) {
            NetworkInterface n = ns.nextElement();
            Enumeration<InetAddress> is = n.getInetAddresses();
            while (is.hasMoreElements()) {
                InetAddress i = is.nextElement();
                if (!i.isLoopbackAddress() && !i.isLinkLocalAddress() && !i.isMulticastAddress()
                        && !isSpecialIp(i.getHostAddress())) addrs.add(i);
            }
        }
        return addrs;
    }

    public static Set<String> resolveLocalIps() {
        Set<InetAddress> addrs = resolveLocalAddresses();
        Set<String> ret = new HashSet<String>();
        for (InetAddress addr : addrs)
            ret.add(addr.getHostAddress());
        return ret;
    }

    public static String getNormalIp() {
        Set<String> ipSet = resolveLocalIps();
        for(String ipStr : ipSet) {
            logger.info(ipStr);
            if(isSpecialIp(ipStr))
                continue;
            return ipStr;
        }
        return "";
    }

    private static boolean isSpecialIp(String ip) {
        if (ip.contains(":")) return true;
        if (ip.startsWith("127.")) return true;
        if (ip.startsWith("169.254.")) return true;
        if (ip.equals("255.255.255.255")) return true;
        return false;
    }
}

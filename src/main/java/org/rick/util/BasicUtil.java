package org.rick.util;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author liyubin
 * @version 1.0
 * @company Netease
 * @description
 */
public class BasicUtil {

    private static Logger logger = Logger.getLogger(BasicUtil.class.getClass());

    /**
     * 将字段解析为int值，适用于level/vip等
     * @param obj
     * @return
     */
    public static int trans2int(Object obj) {

        try {
            if(obj instanceof Number)
                return ((Number) obj).intValue();
            else
//                return Integer.parseInt(obj.toString());
                return (int)Double.parseDouble(obj.toString());
        }catch (Exception e){
            return -1;
        }

    }

    /**
     * 取得机器的hostName
     *
     * @return hostName
     */
    public static String getHostName() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException uhe) {
            String host = uhe.getMessage(); // host = "hostname: hostname"
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            logger.error("get host name fails");
            return "unknown-host";
        }
    }


    public static void main(String... args){
        String a="123";
        int o1 = trans2int(a);
        String b="";
        int o2 = trans2int(b);
        System.out.println( o2 );
        int c = 7;
        int o3 = trans2int(c);
        System.out.println( o3 );

    }
}

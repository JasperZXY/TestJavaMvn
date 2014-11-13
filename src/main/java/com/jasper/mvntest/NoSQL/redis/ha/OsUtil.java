package com.jasper.mvntest.NoSQL.redis.ha;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OsUtil {
    /**
     * ping通某个ip所需的时间是否小于limitTime
     * @param ip
     * @param isWindows
     * @param limitTime 毫秒数
     * @return
     */
    public static boolean pingAccess(String ip, float limitTime) {
        return getReachTime(ip) <= limitTime;
    }
    
    private static boolean isWindows() {
        String osName = System.getProperty("os.name");
        osName = osName.toLowerCase();
        return osName.startsWith("windows");
    }
    
    public static float getReachTime(String ip) {
        boolean isWindows = isWindows();
        Runtime runtime = Runtime.getRuntime();
        Process process;
        float sum = 0;
        String lessOneMs = "<1ms";
        String pattenStr = isWindows ? "=(\\d+)ms" : "=(\\d+.\\d+) ms";
        Pattern p = Pattern.compile(pattenStr);
        String ping = isWindows ? "ping -n 5 " : "ping -c 5 ";
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            process = runtime.exec(ping + ip);
            byte[] b = new byte[1024];
            is = process.getInputStream();
            bis = new BufferedInputStream(is);
            boolean firstLine = true;
            while (bis.read(b) != -1) {
                if (! firstLine) {
                    String info = new String(b);
                    if (info.indexOf(lessOneMs) < 0) {
                        Matcher m = p.matcher(info);
                        if(m.find()){
                            sum += Float.valueOf(m.group(1));
                        }
                    }
                }
                firstLine = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sum / 5;
    }
    
    public static void main(String[] args) {
        System.out.println(System.getProperty("os.name"));
        System.out.println(isWindows());
        System.out.println(getReachTime("127.0.0.1"));
        System.out.println(getReachTime("172.19.103.102"));
        System.out.println(getReachTime("183.61.2.143"));
    }

}

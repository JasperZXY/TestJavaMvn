package com.zxy.demo.NoSQL.redis.ha;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;

/**
 * 测试网络连通性，感觉都不好
 * 
 * @author Jasper
 */
public class Telnet {
    public static void main(String[] args) {
        System.out.println("=================InetAddress================");
        String[] ips = {"127.0.0.1", "172.19.103.102", "183.61.2.143", "14.17.109.239", "14.17.119.177"};
        try {
            InetAddress address = null;
            for(String ip : ips) {
                address = InetAddress.getByName(ip);
                System.out.println(ip + "    1ms " + address.isReachable(1));
                System.out.println(ip + "   10ms " + address.isReachable(10));
                System.out.println(ip + "  100ms " + address.isReachable(100));
                System.out.println(ip + " 1000ms " + address.isReachable(1000));
                System.out.println(ip + " 3000ms " + address.isReachable(3000));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("=================TelnetClient================");
        String[] hosts = {"183.61.2.143", "183.61.2.143", "172.19.108.127"};
        int[] ports = {80, 6380, 23679};
        for (int i=0; i<hosts.length; i++) {
            System.out.println(hosts[i] + "    1ms " + isConnect(hosts[i], ports[i], 1));
            System.out.println(hosts[i] + "   10ms " + isConnect(hosts[i], ports[i], 10));
            System.out.println(hosts[i] + "  100ms " + isConnect(hosts[i], ports[i], 100));
            System.out.println(hosts[i] + " 1000ms " + isConnect(hosts[i], ports[i], 1000));
        }
        
        System.out.println("=================Socket================");
        for (int i=0; i<hosts.length; i++) {
            System.out.println(hosts[i] + "    1ms " + isReachable(hosts[i], ports[i], 1));
            System.out.println(hosts[i] + "   10ms " + isReachable(hosts[i], ports[i], 10));
            System.out.println(hosts[i] + "  100ms " + isReachable(hosts[i], ports[i], 100));
            System.out.println(hosts[i] + " 1000ms " + isReachable(hosts[i], ports[i], 1000));
        }
    }
    
    private static boolean isConnect(String host, int port, int timeout) {
        TelnetClient client = new TelnetClient();
        try {
            client.setDefaultTimeout(timeout);
            client.connect(host, port);
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private static boolean isReachable(String host, int port, int timeout) {
        Socket socket = new Socket();
        SocketAddress address = new InetSocketAddress(host, port);
        try {
            socket.connect(address, timeout);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}

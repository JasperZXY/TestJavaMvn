package com.zxy.demo.jetty.v1;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * 用 Jetty 跑一个最简单是 Servlet，方便后面演示其他东西
 *
 * 依赖 jetty-distribution
 */
public class App {

    public static void main(String[] args) throws Throwable {
        Server server = new Server(8080);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(HelloServlet.class, "/*");

        server.start();
        server.join();
    }

}

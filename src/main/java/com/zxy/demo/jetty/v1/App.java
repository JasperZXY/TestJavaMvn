package com.zxy.demo.jetty.v1;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


/**
 * 用 Jetty 跑一个最简单是 Servlet，方便后面演示其他东西
 *
 * 依赖 jetty-webapp，可以运行就启动Servlet
 * 之前是jetty-distribution，没法运行就启动Servlet
 */
public class App {

    public static void main(String[] args) throws Throwable {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new HelloServlet()), "/hello");

        server.start();
        server.join();
    }

}

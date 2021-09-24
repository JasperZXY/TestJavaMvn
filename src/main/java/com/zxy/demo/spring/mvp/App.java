package com.zxy.demo.spring.mvp;

import com.zxy.demo.spring.mvp.core.ZxyDispatcherServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


/**
 * 一个精简版的Spring-MVC模仿版本，只需依赖Jetty + Servlet
 *
 * 模仿腾讯课堂中的咕炮学院的Spring核心原理剖析
 */
public class App {

    public static void main(String[] args) throws Throwable {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServletHolder holder = new ServletHolder(new ZxyDispatcherServlet());
        holder.setInitOrder(1);
        context.addServlet(holder, "/*");

        server.start();
        server.join();
    }

}

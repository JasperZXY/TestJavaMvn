package com.zxy.demo.spring.mvp;

import com.zxy.demo.spring.mvp.core.ZxyDispatcherServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * 一个精简版的Spring-MVC模仿版本，只需依赖Jetty + Servlet
 */
public class App {

    public static void main(String[] args) throws Throwable {
        Server server = new Server(8080);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(ZxyDispatcherServlet.class, "/*");

        server.start();
        server.join();
    }

}

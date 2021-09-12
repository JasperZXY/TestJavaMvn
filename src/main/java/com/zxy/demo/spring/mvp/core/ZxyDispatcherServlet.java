package com.zxy.demo.spring.mvp.core;

import com.zxy.demo.spring.mvp.annotation.ZxyAutowired;
import com.zxy.demo.spring.mvp.annotation.ZxyController;
import com.zxy.demo.spring.mvp.annotation.ZxyRequestMapping;
import com.zxy.demo.spring.mvp.annotation.ZxyService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class ZxyDispatcherServlet extends HttpServlet {

    private final String DEFAULT_PROPERTY_FILE_NAME = "spring-mvp.property";

    private final String KEY_SCAN_PACKAGE = "scanPackage";

    private Properties properties;

    private List<String> classNames = new ArrayList<>();

    private Map<String, Object> ioc = new HashMap<>();

    private Map<String, Method> requestMapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatcher(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatcher(req, resp);
    }

    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();
        Method method = requestMapping.get(url);
        if (Objects.isNull(method)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Object obj = ioc.get(toLowerFirstCase(method.getDeclaringClass().getSimpleName()));
        try {
            method.invoke(obj, req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("========== ZxyDispatcherServlet init");

        // 1、加载配置文件
        doLoadConfig();

        // 2、扫描相关类
        doScanner(properties.getProperty(KEY_SCAN_PACKAGE));

        // 3、实例化相关类
        doInstance();

        // 4、依赖注入
        doAutowired();

        // 5、RequestMapping 初始化
        doInitRequestMapping();

        System.out.println("========== ZxyDispatcherServlet init end");

    }

    private void doInitRequestMapping() {
        try {
            for (Map.Entry<String, Object> entry : ioc.entrySet()) {
                Class<?> clazz = entry.getValue().getClass();
                if (!clazz.isAnnotationPresent(ZxyController.class)) {
                    continue;
                }

                String baseUrl = "";

                if (entry.getValue().getClass().isAnnotationPresent(ZxyRequestMapping.class)) {
                    ZxyRequestMapping zxyRequestMapping = clazz.getAnnotation(ZxyRequestMapping.class);
                    baseUrl = zxyRequestMapping.value();
                }

                for (Method method : clazz.getMethods()) {
                    if (method.isAnnotationPresent(ZxyRequestMapping.class)) {
                        ZxyRequestMapping zxyRequestMapping = method.getAnnotation(ZxyRequestMapping.class);
                        String url = baseUrl + zxyRequestMapping.value();
                        requestMapping.put(url, method);
                    }
                }

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAutowired() {
        try {
            for (Map.Entry<String, Object> entry : ioc.entrySet()) {
                Field[] fields = entry.getValue().getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (!field.isAnnotationPresent(ZxyAutowired.class)) {
                        continue;
                    }
                    field.setAccessible(true);
                    field.set(entry.getValue(), ioc.get(field.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doInstance() {
        for (String className : classNames) {
            try {
                Class clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(ZxyController.class)
                        || clazz.isAnnotationPresent(ZxyService.class)) {
                    Object obj = clazz.newInstance();
                    ioc.put(toLowerFirstCase(clazz.getSimpleName()), obj);
                    for (Class i : clazz.getInterfaces()) {
                        ioc.put(toLowerFirstCase(i.getSimpleName()), obj);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String toLowerFirstCase(String name) {
        char[] chars = name.toCharArray();
        chars[0] = (char) (chars[0] + ('a' - 'A'));
        return new String(chars);
    }

    private void doScanner(String scanPackage) {
        URL url = getClass().getClassLoader().getResource(scanPackage.replace('.', '/'));
        File rootFile = new File(url.getFile());
        for (File file : rootFile.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            }else {
                if (file.getName().endsWith(".class")) {
                    String className = file.getName().replace(".class", "");
                    classNames.add(scanPackage + "." + className);
                }else {
                    continue;
                }
            }
        }
    }

    private void doLoadConfig() {
        InputStream is = getClass().getClassLoader().getResourceAsStream(DEFAULT_PROPERTY_FILE_NAME);
        properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

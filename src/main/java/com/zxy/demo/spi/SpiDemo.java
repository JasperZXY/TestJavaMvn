package com.zxy.demo.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * SPI 全称为 (Service Provider Interface) ,是JDK内置的一种服务提供发现机制。
 *
 * 一个服务(Service)通常指的是已知的接口或者抽象类，服务提供方就是对这个接口或者抽象类的实现，
 * 然后按照SPI 标准存放到资源路径META-INF/services目录下，文件的命名为该服务接口的全限定名。
 */
public class SpiDemo {

    public static void main(String[] args) {
        ServiceLoader<SayService> serviceLoader = ServiceLoader.load(SayService.class);
        Iterator<SayService> it = serviceLoader.iterator();
        while (it != null && it.hasNext()) {
            SayService sayService = it.next();
            System.out.println(sayService.getClass().getName() + "---" + sayService.sayHi("zxy"));
        }
    }
}

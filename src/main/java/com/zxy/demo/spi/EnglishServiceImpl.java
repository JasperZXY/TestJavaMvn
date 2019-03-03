package com.zxy.demo.spi;

public class EnglishServiceImpl implements SayService {

    @Override
    public String sayHi(String msg) {

        return "Hello, "+msg;
    }

}
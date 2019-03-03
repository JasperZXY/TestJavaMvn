package com.zxy.demo.spi;

public class ChineseServiceImpl implements SayService {

    @Override
    public String sayHi(String msg) {

        return "你好， "+msg;
    }

}
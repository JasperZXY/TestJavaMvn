package com.zxy.demo.spring.mvp.biz;

import com.zxy.demo.spring.mvp.annotation.ZxyService;

@ZxyService
public class HelloServiceImpl implements HelloService {

    @Override
    public String say(String name) {
        return "Hello " + name;
    }

}

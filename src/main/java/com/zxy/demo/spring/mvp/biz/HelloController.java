package com.zxy.demo.spring.mvp.biz;

import com.zxy.demo.spring.mvp.annotation.ZxyAutowired;
import com.zxy.demo.spring.mvp.annotation.ZxyController;
import com.zxy.demo.spring.mvp.annotation.ZxyRequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ZxyRequestMapping("/hello")
@ZxyController
public class HelloController {

    @ZxyAutowired
    private HelloService helloService;

    @ZxyRequestMapping("/say")
    public void say(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String result = helloService.say(req.getParameter("name"));
        resp.getWriter().print(result);
    }

}

package com.zxy.demo.apache.http;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * http://hc.apache.org/httpclient-3.x/
 * <p>
 * commons包下的HttpClient是历史遗留版本，推荐使用Apache HttpComponents
 *
 * @author zxy
 */
public class HttpTest {
    @Test
    public void get() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("corpid", "todo");
        params.put("corpsecret", "todo");

        System.err.println("1:" + ApacheHttpUtils.httpGet(
                "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=id&corpsecret=secrect"));

        System.err.println("2:" + ApacheHttpUtils.httpGet(
                "https://qyapi.weixin.qq.com/cgi-bin/gettoken", params));
    }

    @Test
    public void getForCommons() {
        Map<String, Object> param = new HashMap<>();
        param.put("corpid", "todo");
        param.put("corpsecret", "todo");

        System.err.println("3:" + CommonsHttpUtils.httpGet(
                "https://qyapi.weixin.qq.com/cgi-bin/gettoken?"
                        + "corpid=id&corpsecret=secrect"));

        System.err.println("4:" + CommonsHttpUtils.httpGet(
                "https://qyapi.weixin.qq.com/cgi-bin/gettoken", param));

    }

    @Test
    public void post() throws Exception {
        System.err.println("1:" + ApacheHttpUtils
                .httpPost("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=id&corpsecret=secrect"));
        Map<String, Object> params2 = new HashMap<>();
        params2.put("corpid", "todo");
        params2.put("corpsecret", "todo");
        System.err.println("2:" + ApacheHttpUtils.httpPost("https://qyapi.weixin.qq.com/cgi-bin/gettoken", params2));
        System.err.println("3:" + ApacheHttpUtils.httpPost("https://qyapi.weixin.qq.com/cgi-bin/gettoken",
                "corpid=id&corpsecret=secrect"));
    }

}

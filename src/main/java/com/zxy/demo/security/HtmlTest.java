package com.zxy.demo.security;

import cn.hutool.core.util.EscapeUtil;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;

/**
 * HTML 安全处理
 */
public class HtmlTest {

    public static void main(String[] args) {
        String str =
                "http://xxx.com/a.jpg\\\"\\u003c/script\\u003e\\u003cscript type='text/javascript' src='http://danger.com/xxx.js' /\\u003e\"";

        // 非富文本采用escape转义
        System.out.println("EscapeUtil:" + EscapeUtil.escape(str));

        // 富文本采用owasp antisamy
        AntiSamy antiSamy = new AntiSamy();
        try {
            Policy policy = Policy.getInstance(HtmlTest.class.getClassLoader()
                    .getResourceAsStream("antisamy-anythinggoes.xml"));
            CleanResults results = antiSamy.scan(str, policy);
            System.out.println("AntiSamy:" + results.getCleanHTML());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}

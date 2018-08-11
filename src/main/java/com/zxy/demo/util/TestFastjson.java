package com.zxy.demo.util;

import com.alibaba.fastjson.JSONObject;

public class TestFastjson {
	public static void main(String[] args) throws Exception {
		String str1 = "{'name':'cxh','sex':'1'}";

		JSONObject obj = (JSONObject) JSONObject.parse(str1);
        System.out.println(obj);
        System.out.println(obj.getClass());
        String name = obj.getString("name");
        Object nameo = obj.get("name");
        int age = obj.getIntValue("sex");

        System.out.println(age);
	}

}

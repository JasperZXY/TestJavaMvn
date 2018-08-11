package com.zxy.demo.apache.other;

import java.util.Map;

import org.apache.thrift.TException;

public class UserServiceImpl implements UserService.Iface{
	
	public int userMethod(String type, User user, Map<String, String> year)
			throws TException {
		return Integer.MAX_VALUE;
	}
}

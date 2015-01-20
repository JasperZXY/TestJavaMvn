package com.jasper.mvntest.apache.other;

import org.apache.thrift.TException;

public class PingServiceImpl implements PingService.Iface {
	
	public int ping(int length) throws TException {
		System.out.println("ping start!!! length is :" + length);
		return length;
	}
}

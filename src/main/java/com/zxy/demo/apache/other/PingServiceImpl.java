package com.zxy.demo.apache.other;

import org.apache.thrift.TException;


public class PingServiceImpl implements PingService.Iface {
	
    public int ping(int length) throws TException {
//      throw new TTransportException(TTransportException.TIMED_OUT, "Ping Service Response TimeOut");
        System.out.println("ping start!!! length is :" + length);
        return length;
    }
}

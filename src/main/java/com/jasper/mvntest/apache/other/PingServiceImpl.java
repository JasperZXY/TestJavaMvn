package com.jasper.mvntest.apache.other;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

public class PingServiceImpl implements PingService.Iface {
	
    public int ping(int length) throws TException {
//      throw new TTransportException(TTransportException.TIMED_OUT, "Ping Service Response TimeOut");
        System.out.println("ping start!!! length is :" + length);
        return length;
    }
}

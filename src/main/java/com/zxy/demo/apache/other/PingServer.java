package com.zxy.demo.apache.other;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

public class PingServer {
	
    public static void main(String[] args) {
        try {
            TNonblockingServerSocket socket = new TNonblockingServerSocket(7911);
            PingService.Processor processor = new PingService.Processor(new PingServiceImpl());
            TNonblockingServer.Args arg = new TNonblockingServer.Args(socket);
            arg.protocolFactory(new TBinaryProtocol.Factory());  
            arg.transportFactory(new TFramedTransport.Factory());  
            arg.processorFactory(new TProcessorFactory(processor));  
            TServer server = new TNonblockingServer(arg);  
            server.serve();  
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}

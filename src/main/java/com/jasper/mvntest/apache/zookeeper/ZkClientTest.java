package com.jasper.mvntest.apache.zookeeper;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.github.zkclient.IZkChildListener;
import com.github.zkclient.IZkDataListener;
import com.github.zkclient.IZkStateListener;
import com.github.zkclient.ZkClient;

public class ZkClientTest {
    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181,127.0.0.1:2182");
        
        zkClient.subscribeChildChanges("/root", new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                System.out.println("subscribeChildChanges parentPath: " + parentPath + "  currentChildren: " + currentChildren);
            }
        });
        
        zkClient.subscribeDataChanges("/root", new IZkDataListener() {
            
            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("handleDataDeleted path:" + dataPath);
            }
            
            @Override
            public void handleDataChange(String dataPath, byte[] data) throws Exception {
                System.out.println("handleDataChange path: " + dataPath + " data:" + new String(data));
            }
        });
        
        zkClient.subscribeStateChanges(new IZkStateListener() {
            
            @Override
            public void handleStateChanged(KeeperState state) throws Exception {
                System.out.println("handleStateChanged state:" + state);
            }
            
            @Override
            public void handleNewSession() throws Exception {
                System.out.println("handleNewSession");
            }
        });
        
//        try {
//            ZooKeeper zk = zkClient.getZooKeeper();
//            System.out.println("create root");
//            zk.create("/root", "你好".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//            
//            System.out.println("setData root");
//            zk.setData("/root", "new".getBytes(), -1);
//            
//            System.out.println("create /root/node");
//            zk.create("/root/node", "node".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//            
//            System.out.println("delete /root/node");
//            zk.delete("/root/node", -1);
//            
//            System.out.println("delete /root");
//            zk.delete("/root", -1);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
        
        while(true) {
            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }

}

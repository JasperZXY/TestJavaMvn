package com.jasper.mvntest.apache.zookeeper;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZKUtil;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

public class ZookeeperTest {
    
    public static void main(String[] args) throws Exception {
        // 创建一个Zookeeper实例，第一个参数为目标服务器地址和端口，第二个参数为Session超时时间，第三个为节点变化时的回调方法
        final ZooKeeper zk = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182", 500000, new Watcher() {
            // 监控所有被触发的事件
            @Override
            public void process(WatchedEvent event) {
                System.out.printf("type:%s, path:%s, state:%s\n", event.getType(), event.getPath(), event.getState());
            }
        });
        
        // 创建一个节点root，数据是mydata,不进行ACL权限控制，节点为永久性的(即客户端shutdown了也不会消失)
        zk.create("/root", "mydata".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        
        // 在root下面创建一个childone znode,数据为childone,不进行ACL权限控制，节点为永久性的
        zk.create("/root/childone", "childone".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        // 取得/root节点下的子节点名称,返回List<String>
        System.out.println(zk.getChildren("/root", true));

        // 取得/root/childone节点下的数据,返回byte[]
        System.out.println(new String(zk.getData("/root/childone", true, null)));

        // 修改节点/root/childone下的数据，第三个参数为版本，如果是-1，那会无视被修改的数据版本，直接改掉
        zk.setData("/root/childone", "childonemodify".getBytes(), -1);

        // 删除/root/childone这个节点，第二个参数为版本，－1的话直接删除，无视版本
        zk.delete("/root/childone", -1);
        
        ZKUtil.deleteRecursive(zk, "/root");

        // 关闭session
        zk.close();
    }
    
    @Test
    public void testWatch() throws Exception {
        // 创建一个Zookeeper实例，第一个参数为目标服务器地址和端口，第二个参数为Session超时时间，第三个为节点变化时的回调方法
        final ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 500000, new Watcher() {
            // 监控所有被触发的事件
            @Override
            public void process(WatchedEvent event) {
                System.out.printf("=====zk type:%s, path:%s, state:%s\n", event.getType(), event.getPath(), event.getState());
            }
        });
        
        System.out.println("create root");
        // 创建一个节点root，数据是mydata,不进行ACL权限控制，节点为永久性的(即客户端shutdown了也不会消失)
        zk.create("/root", "mydata".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        Thread.sleep(100);
        System.out.println("create root1");
        zk.create("/root1", "mydata".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        
        zk.getData("/root", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.printf("=====root getData type:%s, path:%s, state:%s\n", event.getType(), event.getPath(), event.getState());
            }
        }, new DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.printf("rc:%s, path:%s, ctx:%s, data:%s, stat:%s", rc, path, ctx, new String(data), stat);
                try {
                    System.out.println("getData:" + new String(zk.getData(path, null, null)));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, null);

        System.out.println("set to mydata2");
        zk.setData("/root", "mydata2".getBytes(), -1);
        
        Thread.sleep(100);
        System.out.println("set to mydata3");
        zk.setData("/root", "mydata3".getBytes(), -1);
        
        System.out.println("test getData: " + new String(zk.getData("/root", false, null)));
        
        ZKUtil.deleteRecursive(zk, "/root");
        ZKUtil.deleteRecursive(zk, "/root1");

        // 关闭session
        zk.close();
        
        System.out.println("======end======");
    }

}

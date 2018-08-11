package com.zxy.demo.apache.zookeeper;

import org.apache.zookeeper.server.LogFormatter;
import org.apache.zookeeper.server.SnapshotFormatter;

/**
 * 用zk自带的工具看文件内容
 * LogFormatter看日志
 * SnapshotFormatter看快照内容
 * @author Jasper
 */
public class FileShowTest {
    public static void main(String[] args) {
        try {
            LogFormatter.main(new String[]{"F:\\zookeeper\\server1\\log\\version-2\\log.100000001"});
            System.out.println("==========");
            SnapshotFormatter.main(new String[]{"F:\\zookeeper\\server1\\data\\version-2\\snapshot.0"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

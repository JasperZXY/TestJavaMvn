package com.jasper.mvntest.NoSQL.mongo;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoCommand {
    public static void main(String[] args) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient("127.0.0.1", 27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = mongoClient.getDB("test");
        
        System.out.println(db.command(new BasicDBObject("getLastError", 1)));
        
        db.getCollection("user").save(new BasicDBObject("_id", 100).append("name", "test").append("age", 12));
        System.out.println(db.getCollection("user").findOne(new BasicDBObject("_id", 100)));  //这里的age是12
        
        BasicDBObject cmd = new BasicDBObject("findAndModify", "user");
        cmd.append("query", new BasicDBObject("_id", 100));
        cmd.append("update", new BasicDBObject("$set", new BasicDBObject("age", 15)));
        CommandResult result = db.command(cmd);
        System.out.println(result);
        System.out.println(((BasicDBObject)(result.get("value"))).get("age"));   //这里的age还是旧的12
        
        System.out.println(db.getCollection("user").findOne(new BasicDBObject("_id", 100)));  //这里的age变成了15
        
    }

}

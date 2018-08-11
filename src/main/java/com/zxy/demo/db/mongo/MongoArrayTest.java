package com.zxy.demo.db.mongo;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

public class MongoArrayTest {
    
    public static void main(String[] args) {
        MongoClient mongoClient = null;
        DBCollection userDbCollection = null;
        try {
//            mongoClient = new MongoClient("127.0.0.1", 27017);
            mongoClient = new MongoClient("183.61.2.143", 27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = mongoClient.getDB("test");
        userDbCollection = db.getCollection("testArray");
        
        System.out.println(userDbCollection);
        System.out.println(userDbCollection.findOne(new BasicDBObject("name", "Jasper")));
        
        WriteResult result = userDbCollection.update(new BasicDBObject("name", "Jasper"), 
                new BasicDBObject("$push", 
                        new BasicDBObject("scores", 
//                                new BasicDBObject("$slice", -3)
//                                .append("$each", new String[]{"9"})
                                new BasicDBObject("$each", new String[]{"15"})
                                .append("$slice", -3)
                                )));
        
        System.out.println(result);
        System.out.println(userDbCollection.findOne(new BasicDBObject("name", "Jasper")));
    }

}

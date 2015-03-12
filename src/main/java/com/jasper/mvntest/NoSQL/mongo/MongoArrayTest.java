package com.jasper.mvntest.NoSQL.mongo;

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
            mongoClient = new MongoClient("127.0.0.1", 27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = mongoClient.getDB("test");
        userDbCollection = db.getCollection("user");
        
        System.out.println(userDbCollection);
        System.out.println(userDbCollection.findOne(new BasicDBObject("name", "Jasper")));
        
        WriteResult result = userDbCollection.update(new BasicDBObject("name", "Jasper"), 
                new BasicDBObject("$push", 
                        new BasicDBObject("scores", 
                                new BasicDBObject("$slice", -3)
                                .append("$each", new int[]{21}))));
        
        System.out.println(result);
        System.out.println(userDbCollection.findOne(new BasicDBObject("name", "Jasper")));
    }

}

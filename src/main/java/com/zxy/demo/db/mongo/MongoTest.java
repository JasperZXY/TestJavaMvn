package com.zxy.demo.db.mongo;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

import com.zxy.demo.util.TimeUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;

public class MongoTest {
    
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
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.put("a").in("11");
        System.out.println(queryBuilder.get());
        
        System.out.println("===========date=========");
        DBCursor cursor;
        try {
//            cursor = userDbCollection.find(new BasicDBObject("birth", new BasicDBObject("$gt", TimeUtil.get(1, "2015-03-24 22:00:00"))));
            cursor = userDbCollection.find(new BasicDBObject("birth", new BasicDBObject("$gt", TimeUtil.get(1, "2015-03-24 22:00:00"))));
            while (cursor.hasNext()) {
                DBObject object = cursor.next();
                System.out.println(object);
                System.out.println(TimeUtil.get(1, (Date) object.get("birth")));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(Long.toHexString(System.currentTimeMillis() / 1000));
        
        try {
            System.out.println(userDbCollection.findOne(new BasicDBObject("birth", TimeUtil.get(1, "2015-10-10 08:00:00"))));   //时间精确查询
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        System.out.println("==========");
        BasicDBList list = new BasicDBList();
        list.add(new BasicDBObject("name", "test1"));
        list.add(new BasicDBObject("age", 15));
        System.out.println(userDbCollection.findOne(new BasicDBObject("$or", list)));   //or运算
        
        System.out.println(userDbCollection.findOne(new BasicDBObject("age", new BasicDBObject("$mod", new int[]{5, 2}))));  //除5模2
        
        Pattern pattern = Pattern.compile("^Jasper[0-9]{1}");
        System.out.println(userDbCollection.findOne(new BasicDBObject("name", pattern)));   //正则表达式
        DBObject user = userDbCollection.findOne(new BasicDBObject("name", pattern));
        System.out.println(((int)((BasicDBList)(user.get("scores"))).get(0)));
        
        System.out.println("================");
        DBCollection counterCollection = db.getCollection("counter");
        if (counterCollection.findOne(new BasicDBObject("_id", 2)) == null) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", 2);
            object.put("count", 0);
            counterCollection.insert(object);
        }
        DBObject object = counterCollection.findAndModify(new BasicDBObject("_id", 2), new BasicDBObject("$inc", new BasicDBObject("count", 1)));
        System.out.println(object);
        System.out.println((int)object.get("count"));
//        counterCollection.update(new BasicDBObject("_id", 2), new BasicDBObject("$inc", new BasicDBObject("count", 1)), true, false);
        System.out.println(counterCollection.findOne(new BasicDBObject("_id", 2)));
        
    }

}

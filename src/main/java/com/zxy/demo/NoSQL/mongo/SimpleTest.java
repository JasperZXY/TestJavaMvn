package com.zxy.demo.NoSQL.mongo;

import java.net.UnknownHostException;

import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class SimpleTest {
	public static void main(String[] args) throws UnknownHostException {
//		Mongo mongo = new Mongo();
		MongoClient mongoClient = new MongoClient("172.19.103.92", 27017);
//		MongoClient mongoClient = new MongoClient("localhost", 27017);
		System.out.println("===============所有数据库名================");
		for(String name : mongoClient.getDatabaseNames()) {
			System.out.println(name);
		}
		System.out.println("===============所有数据库名================");
		
		DB db = mongoClient.getDB("mydb");
		System.out.println("===============数据库mydb下所有collection================");
		for(String name : db.getCollectionNames()) {
			System.out.println(name);
		}
		System.out.println("===============数据库mydb下所有collection================");
		
		DBCollection userCollection = db.getCollection("user");
		System.out.println("===============user collection 下的数据================");
		Cursor cursor = userCollection.find();
		while(cursor.hasNext()) {
			System.out.println(cursor.next());
		}
		System.out.println(JSON.serialize(cursor));
		System.out.println(userCollection.count());
		System.out.println(cursor.getCursorId());
		System.out.println("===============user collection 下的数据================");
	}

}

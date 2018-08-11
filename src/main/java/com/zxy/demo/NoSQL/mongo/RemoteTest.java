package com.zxy.demo.NoSQL.mongo;

import java.net.UnknownHostException;
import java.util.Arrays;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class RemoteTest {
	public static void main(String[] args) throws UnknownHostException {
		ServerAddress serverAddress = new ServerAddress("localhost", 27017);
//		ServerAddress serverAddress = new ServerAddress("172.19.103.92", 27017);
		MongoCredential mongoCredential = MongoCredential.createMongoCRCredential("jasper", "mydb", "123456".toCharArray());
		MongoClient mongoClient = new MongoClient(Arrays.asList(serverAddress), Arrays.asList(mongoCredential));
		System.out.println(mongoClient);
		DB db = mongoClient.getDB("mydb");
		System.out.println(db);
		for(String name : db.getCollectionNames()) {
			System.out.println(name);
		}
	}

}

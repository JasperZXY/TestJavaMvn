package com.zxy.demo.NoSQL.mongo;

import java.net.UnknownHostException;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryOperators;

/**
 * 增删查改操作
 * @author Jasper
 */
public class CURD {
	private final static String NAME = "name";
	private final static String AGE = "age";
	private final static String STATUS = "status";
	private final static String ID = "_id";
	private MongoClient mongoClient = null;
//	private DB db;
	private DBCollection userDbCollection;
	
	public static void main(String[] args) {
		CURD curd = new CURD();
		curd.init();
		
		curd.modifyCol();
//		curd.queryAll();
//		curd.queryAllWithName();
//		curd.findReg();
//		curd.findAgeGt13();
//		curd.findAgeIn13and14();
//		curd.add();
//		curd.queryAll();
//		curd.modify();
//		curd.queryAll();
//		curd.delete();
//		curd.queryAll();
	}
	
	public void init() {
		try {
			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB db = mongoClient.getDB("mydb");
		userDbCollection = db.getCollection("user");
	}
	
	public void queryAll() {
		System.out.println("===============user collection 下的数据================");
		Cursor cursor = userDbCollection.find();
		while(cursor.hasNext()) {
			System.out.println(cursor.next());
		}
		System.out.println("===============user collection 下的数据================");
	}
	
	public void queryAllWithName() {
		System.out.println("===============user collection 下的数据  只显示name ================");
		Cursor cursor = userDbCollection.find(null, new BasicDBObject(NAME, true));
		while(cursor.hasNext()) {
			System.out.println(cursor.next());
		}
		System.out.println("===============user collection 下的数据  只显示name ================");
	}
	
	public void findReg() {
		System.out.println("===============user collection 下的数据 findReg 名字以j开头================");
		DBObject object = new BasicDBObject();
		object.put(NAME, Pattern.compile("^j"));
		Cursor cursor = userDbCollection.find(object);
		while(cursor.hasNext()) {
			System.out.println(cursor.next());
		}
		System.out.println("===============user collection 下的数据 findReg 名字以j开头================");
	}
	
	public void findAgeGt13() {
		System.out.println("===============user collection 下的数据 age > 13================");
		DBObject object = new BasicDBObject();
		object.put(AGE, new BasicDBObject("$gt", 13));
		Cursor cursor = userDbCollection.find(object);
		while(cursor.hasNext()) {
			System.out.println(cursor.next());
		}
		System.out.println("===============user collection 下的数据  age > 13================");
	}
	
	public void findAgeIn13and14() {
		System.out.println("===============user collection 下的数据 age in [13, 14]================");
		DBObject object = new BasicDBObject();
		object.put(AGE, new BasicDBObject(QueryOperators.IN, new int[]{13, 14}));
		Cursor cursor = userDbCollection.find(object);
		while(cursor.hasNext()) {
			System.out.println(cursor.next());
		}
		System.out.println("===============user collection 下的数据 age in [13, 14]================");
	}
	
	public void add() {
		System.out.println("===============add user================");
		DBObject user = new BasicDBObject();
		user.put(NAME, "test");
		user.put(AGE, 14);
		user.put(STATUS, "A");
		System.out.println(userDbCollection.insert(user, new BasicDBObject("name", "test1")));
	}
	
	public void modify() {
		System.out.println("===============modify user================");
		DBObject user = new BasicDBObject();
		user.put(NAME, "test");
		Cursor cursor = userDbCollection.find(user);
		while(cursor.hasNext()) {
			DBObject modifyObject = cursor.next();
			DBObject sourceObject = new BasicDBObject(ID, modifyObject.get(ID));
			modifyObject.put(NAME, "test2");
			System.out.println(userDbCollection.update(sourceObject, modifyObject));
		}
	}
	
	public void modifyCol() {
	    System.out.println("===============modify 只修改某一个字段，其他字段数据不变================");
	    DBObject user = new BasicDBObject();
	    user.put(NAME, "jasper1");
	    DBObject dbObject = new BasicDBObject();
	    DBObject updateField = new BasicDBObject();  //要更改的字段及对应的值
	    updateField.put(AGE, 18);
	    dbObject.put("$set", updateField);
	    DBObject dbObject2 = userDbCollection.findAndModify(user, dbObject);
	    userDbCollection.findAndModify(user, null, null, false, dbObject, false, true);
	    System.out.println(userDbCollection.find(user).limit(1));
	    System.out.println(dbObject2);
	}
	
	public void delete() {
		System.out.println("===============delete user================");
		System.out.println(userDbCollection.count());
		DBObject user = new BasicDBObject();
		user.put(NAME, Pattern.compile("^test"));
		System.out.println(userDbCollection.remove(user));
		System.out.println(userDbCollection.count());
	}

}

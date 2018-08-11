package com.zxy.demo.db.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;


public class SpringMongo {
    public static void main(String[] args) {
        try {
            List<ServerAddress> serverAddresses = new ArrayList<>();
            serverAddresses.add(new ServerAddress("127.0.0.1", 27018));
            serverAddresses.add(new ServerAddress("127.0.0.1", 27019));
            serverAddresses.add(new ServerAddress("127.0.0.1", 27020));
            MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(new MongoClient(new ServerAddress("127.0.0.1", 27020)), "test");
            
//            MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(new MongoClient(new ServerAddress("127.0.0.1", 27020)), "test");
//            mongoDbFactory.getDb().getMongo().setReadPreference(TaggableReadPreference.secondaryPreferred());   //备份接口可以读
            
            MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);
            
            User user = new User();
            user.set_id(2);
            user.setAge(12);
            user.setName("张三");
            user.setList(Collections.singletonList("111"));
//            mongoTemplate.insert(user);
            
            Query query = new Query();
            query.addCriteria(new Criteria("_id").is(2));
            System.out.println(mongoTemplate.findOne(query, User.class).getName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
    static class User {
        private int _id;
        private String name;
        private int age;
        private List<String> list;
        
        public String getName() {
            return name;
        }
        public int getAge() {
            return age;
        }
        public void setName(String name) {
            this.name = name;
        }
        public void setAge(int age) {
            this.age = age;
        }
        public int get_id() {
            return _id;
        }
        public void set_id(int _id) {
            this._id = _id;
        }
        public List<String> getList() {
            return list;
        }
        public void setList(List<String> list) {
            this.list = list;
        }
        
    }
    
}

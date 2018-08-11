package com.zxy.demo.apache;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zxy.demo.apache.domain.Person;


public class TestBeanUtils {
	public static void main(String[] args) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		Person person = new Person();
		person.setName("tom");
		person.setAge(21);
		try {
			// 克隆
			Person person2 = (Person) BeanUtils.cloneBean(person);
			System.out.println(person2.getName() + ">>" + person2.getAge());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		System.out.println("========================================");
		// 原理也是通过Java的反射机制来做的。
		// 2、 将一个Map对象转化为一个Bean
		// 这个Map对象的key必须与Bean的属性相对应。
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "tom");
		map.put("email", "tom@");
		map.put("age", 21);
		// 将map转化为一个Person对象
		Person person3 = new Person();
		BeanUtils.populate(person3, map);
		System.out.println("map->" + map);
		System.out.println("name->" + person3.getName());
		// 通过上面的一行代码，此时person的属性就已经具有了上面所赋的值了。
		// 将一个Bean转化为一个Map对象了，如下：
		Map<?, ?> map2 = BeanUtils.describe(person3);
		System.out.println("map2->" + map2);
	}

}

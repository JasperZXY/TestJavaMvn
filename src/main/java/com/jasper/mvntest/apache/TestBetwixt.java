package com.jasper.mvntest.apache;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.xml.sax.SAXException;

import com.jasper.mvntest.apache.domain.Person;

/**
 * XML与Java对象之间相互转换
 * 
 * @author Jasper
 */
public class TestBetwixt {
	public static void main(String[] args) throws IOException, SAXException,
			IntrospectionException {
		toXML();
		System.out.println("------------------------------------------------");
		toBean();
	}

	public static void toXML() throws IOException, SAXException,
			IntrospectionException {
		// 先创建一个StringWriter，我们将把它写入为一个字符串
		StringWriter outputWriter = new StringWriter();
		// Betwixt在这里仅仅是将Bean写入为一个片断
		// 所以如果要想完整的XML内容，我们应该写入头格式
		outputWriter.write("<?xml version='1.0' encoding='UTF-8' ?>\n");
		// 创建一个BeanWriter，其将写入到我们预备的stream中
		BeanWriter beanWriter = new BeanWriter(outputWriter);
		// 配置betwixt
		// 更多详情请参考java docs 或最新的文档
		beanWriter.getXMLIntrospector().getConfiguration()
				.setAttributesForPrimitives(false);
		beanWriter.getBindingConfiguration().setMapIDs(false);
		beanWriter.enablePrettyPrint();
		// 如果这个地方不传入XML的根节点名，Betwixt将自己猜测是什么
		// 但是让我们将例子Bean名作为根节点吧
		Person person = new Person();
		person.setAge(12);
		person.setName("钟献耀");
		person.setEmail("a@163.com");
		beanWriter.write("person", person);
		// 输出结果
		System.out.println(outputWriter.toString());
		// Betwixt写的是片断而不是一个文档，所以不要自动的关闭掉writers或者streams，
		// 但这里仅仅是一个例子，不会做更多事情，所以可以关掉
		outputWriter.close();
	}

	public static void toBean() throws IOException, SAXException,
			IntrospectionException {
		// 先创建一个XML，由于这里仅是作为例子，所以我们硬编码了一段XML内容
		StringReader xmlReader = new StringReader(
				"<?xml version='1.0' encoding='UTF-8' ?> <person><age>25</age><name>James Smith</name><email>12@153.com</email></person>");
		// 创建BeanReader
		BeanReader beanReader = new BeanReader();
		// 配置reader
		beanReader.getXMLIntrospector().getConfiguration()
				.setAttributesForPrimitives(false);
		beanReader.getBindingConfiguration().setMapIDs(false);
		// 注册beans，以便betwixt知道XML将要被转化为一个什么Bean
		beanReader.registerBeanClass("person", Person.class);
		// 现在我们对XML进行解析
		Person person = (Person) beanReader.parse(xmlReader);
		// 输出结果
		System.out.println(person.getName() + " " + person.getAge() + " " + person.getEmail());
	}

}

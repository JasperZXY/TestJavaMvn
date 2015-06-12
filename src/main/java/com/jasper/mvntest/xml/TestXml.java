package com.jasper.mvntest.xml;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.MapUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jasper.mvntest.xml.bean.Classes;
import com.jasper.mvntest.xml.bean.ListBean;
import com.jasper.mvntest.xml.bean.Student;
import com.thoughtworks.xstream.XStream;

public class TestXml {
    private static final String PATH = "F:\\tmp\\";
    private XStream xstream = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    private Student bean = null;

    /**
     * 初始化资源准备
     */
    @Before
    public void init() {
        try {
            xstream = new XStream();
            // xstream = new XStream(new DomDriver()); // 需要xpp3 jar
        } catch (Exception e) {
            e.printStackTrace();
        }
        bean = new Student();
        bean.setId(1);
        bean.setName("jack");
        bean.setBirthday(new Date());
    }

    /**
     * 释放对象资源
     */
    @After
    public void destory() {
        xstream = null;
        bean = null;
        try {
            if (out != null) {
                out.flush();
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.gc();
    }
    
    public final void print(String string) {
        System.out.println(string);
    }

    public final void fail(String string) {
        System.err.println(string);
    }
    
    /**
     * Java对象转换成XML字符串
     */
    @Test
    public void writeBean2XML() {
        try {
            print("------------Bean->XML------------");
            print(xstream.toXML(bean));
            print("重命名后的XML");
            //类重命名
            xstream.alias("学生", Student.class);
            //属性重命名
            xstream.aliasField("生日", Student.class, "birthday");
            xstream.aliasField("名字", Student.class, "name");
            //包重命名
//            xstream.aliasPackage("xml", "com.jasper.mvntest.xml.bean");
            print(xstream.toXML(bean));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 将Java的List集合转换成XML对象
     */
    @Test
    public void writeList2XML() {
        try {
            //修改元素名称
            xstream.alias("beans", ListBean.class);
            xstream.alias("student", Student.class);
            print("----------List-->XML----------");
            ListBean listBean = new ListBean();
            listBean.setName("this is a List Collection");
            
            List<Object> list = new ArrayList<Object>();
            list.add(bean);
            list.add(bean);//引用bean
            //list.add(listBean);//引用listBean，父元素
            
            bean = new Student();
            bean.setAddress("china");
            bean.setEmail("tom@125.com");
            bean.setId(2);
            bean.setName("tom");
            bean.setBirthday(new Date());
            
            list.add(bean);
            listBean.setList(list);
            
            //将ListBean中的集合设置空元素，即不显示集合元素标签
            //xstream.addImplicitCollection(ListBean.class, "list");
            
            //设置reference模型
            //xstream.setMode(XStream.NO_REFERENCES);//不引用
//            xstream.setMode(XStream.ID_REFERENCES);//id引用
            //xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);//绝对路径引用
              
            //将name设置为父类（Student）的元素的属性
            xstream.useAttributeFor(Student.class, "name");
//            xstream.useAttributeFor(Birthday.class, "birthday");
            //修改属性的name
            xstream.aliasAttribute("姓名", "name");
//            xstream.aliasField("生日", Birthday.class, "birthday");
          
            print(xstream.toXML(listBean));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void writeList2XML4Annotation() {
        try {
            print("---------annotation Bean --> XML---------");
            Student stu = new Student();
            stu.setName("jack");
            Classes c = new Classes();
            c.setStudents(Collections.singletonList(stu));
            c.setName("一班");
            c.setNumber(2);
            //对指定的类使用Annotation
//            xstream.processAnnotations(Classes.class);
            //启用Annotation
            xstream.autodetectAnnotations(true);
            xstream.alias("student", Student.class);
            print(xstream.toXML(c));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

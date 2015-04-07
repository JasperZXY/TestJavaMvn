package com.jasper.mvntest.xml.bean;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("class")
public class Classes {
    /*
     * 设置属性显示
     */
    @XStreamAsAttribute
    @XStreamAlias("名称")
    private String name;
    
    /*
     * 忽略
     */
    @XStreamOmitField
    private int number;
    
    @XStreamImplicit(itemFieldName = "Students")
    private List<Student> students;

}

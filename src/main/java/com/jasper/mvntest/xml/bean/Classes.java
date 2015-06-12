package com.jasper.mvntest.xml.bean;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("班级")
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
    
    @XStreamImplicit(itemFieldName = "所以学生")
    private List<Student> students;

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

}

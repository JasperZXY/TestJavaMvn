package com.jasper.mvntest.guava;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TestList {
	public static void main(String[] args) {
		List<String> list = Lists.newArrayList();
		list.add("1");
		System.out.println(list);
		list = Lists.newArrayList("1", "2", "3");
		System.out.println(list);
		
		ImmutableList<Integer> immutableList = ImmutableList.of(1, 2, 3);
		System.out.println(immutableList);
	}

}

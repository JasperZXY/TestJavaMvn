package com.zxy.demo.guava;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TestList {
	public static void main(String[] args) {
		List<String> list = Lists.newArrayList();
		list.add("1");
		System.out.println(list);
		list = Lists.newArrayList("1", "2", "3");
		System.out.println(list);
		
		ImmutableList<Integer> immutableList = ImmutableList.of(1, 2, 3);
		System.out.println(immutableList);
		
		List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
		System.out.println(Lists.partition(list2, 3));
	}

}

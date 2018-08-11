package com.zxy.demo.guava;

import com.google.common.collect.ImmutableMap;

public class TestMap {
	public static void main(String[] args) {
		ImmutableMap<String, Integer> map = ImmutableMap.of(
				"1", 1, 
				"2", 2,
				"3", 3);
		System.out.println(map);
		
		ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();  
		builder.put("1", 1);
		builder.put("2", 2);
		ImmutableMap<String, Integer> map2 = builder.build(); 
		System.out.println(map2);
	}
}

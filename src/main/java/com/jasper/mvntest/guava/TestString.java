package com.jasper.mvntest.guava;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class TestString {
	public static void main(String[] args) {
		String[] strings = new String[] { "1", "22", "333" };
		String joined = Joiner.on(',').useForNull("NA").join(strings);
		System.out.println(joined);

		for (String word : Splitter.on(',').split("ajoo,so,handsome!")) {
			System.out.println(word);
		}
	}

}

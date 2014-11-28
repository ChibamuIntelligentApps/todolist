package com.poop.todo.util;

import org.apache.commons.lang3.RandomStringUtils;
public class TestUtil {
	public static String createStringWithLength(int length){
		return  RandomStringUtils.randomAlphabetic(length);			
	}
}

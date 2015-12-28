package com.yinweilong.support;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.RandomStringUtils;

import flexjson.JSONSerializer;

public class Tools {
	/**
	 * 普通MD5加密
	 * 
	 * @param s
	 * @return
	 */
	public static String md5(String s) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			s = new BigInteger(1, m.digest()).toString(16);
		} catch (NoSuchAlgorithmException ex) {
		}
		return s;
	}

	/**
	 * 用户本项目内部的MD5加密私密算法
	 * 
	 * @param s
	 * @return
	 */
	public static String md5WithYin(String s) {
		return md5(s.trim() + "YINWEILONG");
	}

	/**
	 * 生成随机数
	 * 
	 * @param length
	 * @return
	 */
	public static String generateRandomNumber(int length) {
		return RandomStringUtils.randomNumeric(length);
	}
	
	/**
	 * 把Object转换为json
	 * 
	 * @param o
	 * @return
	 */
	public static String caseObjectToJson(Object o) {
        JSONSerializer serializer = new JSONSerializer();
        return serializer.deepSerialize(o);
    }
	
	public static void main(String agrs[]){
		System.out.println(md5WithYin("qqqqqq"));
		
	}
}

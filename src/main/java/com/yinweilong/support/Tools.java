package com.yinweilong.support;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import flexjson.JSONSerializer;

public class Tools {

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

	/****************************************************************
	 ********************** 字符串处理********************************
	 ****************************************************************
	 */

	/**
	 * 判断字符串是否为空
	 * 
	 * @param src
	 * @return
	 */
	public static boolean isBlank(String src) {
		return StringUtils.isBlank(src);
	}

	/**
	 * 判断字符是否不为空
	 * 
	 * @param src
	 * @return
	 */
	public static boolean isNotBlank(String src) {
		return StringUtils.isNotBlank(src);
	}

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
	 * 获取用户IP地址
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public final static String getIpAddress(HttpServletRequest request) throws IOException {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = (String) ips[index];
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}
		return ip;
	}

}

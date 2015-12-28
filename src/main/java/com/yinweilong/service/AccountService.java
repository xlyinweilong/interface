package com.yinweilong.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yinweilong.entity.User;
import com.yinweilong.repository.UserRepository;
import com.yinweilong.support.Tools;

@Service
public class AccountService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * 产生用户的accessToken
	 * 
	 * @param user
	 */
	public void produceUserSession(User user) {
		user.setAccessToken(Tools.md5WithYin(user.getId() + "_" + System.currentTimeMillis() + "_" + Tools.generateRandomNumber(3)));
	}

	/**
	 * 产生用户的签名密码
	 * 
	 * @param user
	 */
	public void produceUserSecurity(User user) {
		user.setSecurity(Tools.generateRandomNumber(8));
	}


}

package com.yinweilong.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yinweilong.entity.User;
import com.yinweilong.interceptor.AccessRequired;
import com.yinweilong.json.BaseJson;
import com.yinweilong.repository.*;
import com.yinweilong.service.AccountService;
import com.yinweilong.support.Tools;

@RestController
@RequestMapping("/account")
public class AccountRest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private AuthRepository authRepository;
	@Autowired
	private AccountService accountService;

	/**
	 * 用户登录
	 * 
	 * @param account
	 * @param passwd
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public BaseJson login(@RequestParam(value = "account", required = true) String account, @RequestParam(value = "passwd", required = true) String passwd) {
		BaseJson bj = new BaseJson();
		User user = userRepository.findByAccount(account);
		if (user == null || !user.getPasswd().equals(Tools.md5WithYin(passwd))) {
			bj.setMsg("账号或密码错误");
		} else {
			accountService.produceUserSession(user);
			accountService.produceUserSecurity(user);
			userRepository.save(user);
			Map<String, String> map = new HashMap<>();
			// 返回用户的sessionId和用户的签名密码，用HTTPS回传给客户端
			map.put("accessToken", user.getAccessToken());
			map.put("security", user.getSecurity());
			bj.setData(map);
			bj.setSuccess(1);
		}
		return bj;
	}

	/**
	 * 获取用户权限
	 * 
	 * @param sessionId
	 * @param account
	 * @param passwd
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/user_auth", method = RequestMethod.GET)
	public BaseJson userAuth(@CookieValue(value = "accessToken", required = true) String accessToken) {
		BaseJson bj = new BaseJson();
		System.out.println(accessToken);
		return bj;
	}

	/**
	 * 用户列表
	 * 
	 * @param accessToken
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/user_list", method = RequestMethod.GET)
	public BaseJson userList(@CookieValue(value = "accessToken", required = true) String accessToken, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		// new Sort(Sort.Direction.DESC, "createDate")
		return new BaseJson(1, userRepository.findAll(new PageRequest(pageNumber, pageSize)));
	}

	/**
	 * 角色列表
	 * 
	 * @param accessToken
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/role_list", method = RequestMethod.GET)
	public BaseJson roleList(@CookieValue(value = "accessToken", required = true) String accessToken, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		return new BaseJson(1, roleRepository.findAll(new PageRequest(pageNumber, pageSize)));
	}

	@AccessRequired
	@RequestMapping(value = "/auth_list", method = RequestMethod.GET)
	public BaseJson authList(@CookieValue(value = "accessToken", required = true) String accessToken, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		return new BaseJson(1, authRepository.findAll(new PageRequest(pageNumber, pageSize)));
	}

}

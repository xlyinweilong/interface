package com.yinweilong.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yinweilong.entity.Auth;
import com.yinweilong.entity.Role;
import com.yinweilong.entity.User;
import com.yinweilong.interceptor.AccessRequired;
import com.yinweilong.interceptor.SerializedField;
import com.yinweilong.json.BaseJson;
import com.yinweilong.repository.*;
import com.yinweilong.service.AccountService;
import com.yinweilong.support.Tools;
import com.yinweilong.support.enums.AuthType;
import com.yinweilong.support.enums.UserType;

/**
 * REST用户接口
 * 
 * @author yin.weilong
 *
 */
@RestController
@RequestMapping(value = "/account")
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
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@SerializedField(includes = { "accessToken" })
	public BaseJson login(@RequestBody User user) {
		BaseJson bj = new BaseJson();
		User dbUser = userRepository.findByAccount(user.getAccount());
		if (dbUser == null || !dbUser.getPasswd().equals(Tools.md5WithYin(user.getPasswd()))) {
			bj.setMsg("账号或密码错误");
		} else {
			accountService.produceUserSession(dbUser);
			accountService.produceUserSecurity(dbUser);
			userRepository.save(dbUser);
			// 用HTTPS回传给客户端
			bj.setData(dbUser);
			bj.setSuccess(1);
		}
		return bj;
	}

	/**
	 * 用户登出
	 * 
	 * @param accessToken
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public BaseJson logout(@CookieValue(value = "accessToken", required = true) String accessToken) {
		User user = userRepository.findByAccessToken(accessToken);
		user.setAccessToken(null);
		user.setSecurity(null);
		userRepository.save(user);
		return new BaseJson(1);
	}

	/**
	 * 获取用户的权限
	 * 
	 * @param accessToken
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/user_auth", method = RequestMethod.GET)
	@SerializedField(includes = { "className", "methodName", "name", "type", "url" })
	public BaseJson userAuth(@CookieValue(value = "accessToken", required = true) String accessToken) {
		BaseJson bj = new BaseJson();
		List<String> types = new ArrayList<>();
		bj.setSuccess(1);
		User user = userRepository.findByAccessToken(accessToken);
		if (user.getType().equals(UserType.USER.name())) {
			Role role = roleRepository.findOne(user.getRoleId());
			bj.setData(authRepository.findAll(role.getAuthIds()));
		} else if (user.getType().equals(UserType.ADMIN.name())) {
			types.add(AuthType.ADMIN_ACTION.name());
			types.add(AuthType.ADMIN_MENU.name());
			types.add(AuthType.USER_ACTION.name());
			types.add(AuthType.USER_MENU.name());
			bj.setData(authRepository.findByTypeIn(types));
		} else if (user.getType().equals(UserType.ROOT.name())) {
			types.add(AuthType.ROOT_ACTION.name());
			types.add(AuthType.ROOT_MENU.name());
			bj.setData(authRepository.findByTypeIn(types));
		} else {
			bj.setSuccess(-1);
			bj.setMsg("用户信息异常");
		}
		return bj;
	}

	/**
	 * 用户列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/user_list", method = RequestMethod.GET)
	@SerializedField(includes = { "id", "account", "name", "createDate" })
	public BaseJson userList(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		return new BaseJson(1, userRepository.findAll(new PageRequest(pageNumber - 1, pageSize)));
	}

	/**
	 * 批量删除用户
	 * 
	 * @param ids
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/user_delete", method = RequestMethod.PUT)
	public BaseJson userDeleted(@RequestBody String[] ids) {
		userRepository.deleteByIdIn(ids);
		return new BaseJson(1);
	}

	/**
	 * 获取某个用户
	 * 
	 * @param id
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	@SerializedField(includes = { "id", "account", "name", "roleId" })
	public BaseJson userGet(@PathVariable String id) {
		return new BaseJson(1, userRepository.findOne(id));
	}

	/**
	 * 创建/修改用戶
	 * 
	 * @param user
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	@SerializedField(includes = { "id", "account", "name", "roleId" })
	public BaseJson user(@RequestBody User user) {
		if (user.getId() == null) {
			if (Tools.isBlank(user.getAccount())) {
				return new BaseJson(1, "请输入用户账号", null);
			} else if (userRepository.findByAccount(user.getAccount()) != null) {
				return new BaseJson(1, "该账号已经存在", null);
			}
			if (Tools.isBlank(user.getPasswd())) {
				return new BaseJson(1, "请输入密码", null);
			} else {
				user.setPasswd(Tools.md5WithYin(user.getPasswd()));
			}
		} else {
			User dbUser = userRepository.findOne(user.getId());
			if (Tools.isBlank(user.getAccount())) {
				return new BaseJson(1, "请输入用户账号", null);
			} else if (!dbUser.getAccount().equals(user.getAccount()) & userRepository.findByAccount(user.getAccount()) != null) {
				return new BaseJson(1, "该账号已经存在", null);
			}
			if (Tools.isBlank(user.getPasswd())) {
				user.setPasswd(dbUser.getPasswd());
			} else {
				user.setPasswd(Tools.md5WithYin(user.getPasswd()));
			}
		}
		return new BaseJson(1, user.getId() == null ? "创建成功" : "修改成功", user.getId() == null ? userRepository.insert(user) : userRepository.save(user));
	}

	/**
	 * 权限列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/auth_list", method = RequestMethod.GET)
	@SerializedField(includes = { "id", "createDate", "name", "type" })
	public BaseJson authList(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		return new BaseJson(1, authRepository.findAll(new PageRequest(pageNumber - 1, pageSize, new Sort(Sort.Direction.DESC, "createDate"))));
	}

	/**
	 * 所有权限
	 * 
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/auth_all", method = RequestMethod.GET)
	@SerializedField(includes = { "id", "createDate", "name", "className", "methodName", "type", "url" })
	public BaseJson authAll() {
		List<String> types = new ArrayList<>();
		types.add(AuthType.USER_ACTION.name());
		types.add(AuthType.USER_MENU.name());
		return new BaseJson(1, authRepository.findByTypeIn(types));
	}

	/**
	 * 批量删除权限
	 * 
	 * @param ids
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/auth_delete", method = RequestMethod.PUT)
	public BaseJson authDeleted(@RequestBody String[] ids) {
		authRepository.deleteByIdIn(ids);
		return new BaseJson(1);
	}

	/**
	 * 获取某个权限
	 * 
	 * @param id
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/auth/{id}", method = RequestMethod.GET)
	@SerializedField(includes = { "id", "createDate", "name", "className", "methodName", "type", "url" })
	public BaseJson authGet(@PathVariable String id) {
		return new BaseJson(1, authRepository.findOne(id));
	}

	/**
	 * 创建/修改权限
	 * 
	 * @param auth
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	@SerializedField(includes = { "id", "createDate", "name", "className", "methodName", "type", "url" })
	public BaseJson auth(@RequestBody Auth auth) {
		return new BaseJson(1, auth.getId() == null ? "创建成功" : "修改成功", auth.getId() == null ? authRepository.insert(auth) : authRepository.save(auth));
	}

	/**
	 * 角色列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/role_list", method = RequestMethod.GET)
	@SerializedField(includes = { "id", "createDate", "name", "authIds" })
	public BaseJson roleList(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		return new BaseJson(1, roleRepository.findAll(new PageRequest(pageNumber - 1, pageSize, new Sort(Sort.Direction.DESC, "createDate"))));
	}

	/**
	 * 批量删除角色
	 * 
	 * @param ids
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/role_delete", method = RequestMethod.PUT)
	public BaseJson roleDeleted(@RequestBody String[] ids) {
		if (userRepository.countByroleIdIn(ids) > 0) {
			return new BaseJson(0, "删除的角色含有用户", null);
		}
		roleRepository.deleteByIdIn(ids);
		return new BaseJson(1);
	}

	/**
	 * 获取某个角色
	 * 
	 * @param id
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/role/{id}", method = RequestMethod.GET)
	@SerializedField(includes = { "id", "createDate", "name", "authIds" })
	public BaseJson roleGet(@PathVariable String id) {
		return new BaseJson(1, roleRepository.findOne(id));
	}

	/**
	 * 创建/修改角色
	 * 
	 * @param auth
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/role", method = RequestMethod.POST)
	@SerializedField(includes = { "id", "createDate", "name", "authIds" })
	public BaseJson role(@RequestBody Role role) {
		return new BaseJson(1, role.getId() == null ? "创建成功" : "修改成功", role.getId() == null ? roleRepository.insert(role) : roleRepository.save(role));
	}

}

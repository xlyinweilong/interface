package com.yinweilong.interceptor;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yinweilong.entity.Auth;
import com.yinweilong.entity.Role;
import com.yinweilong.entity.User;
import com.yinweilong.json.BaseJson;
import com.yinweilong.repository.AuthRepository;
import com.yinweilong.repository.RoleRepository;
import com.yinweilong.repository.UserRepository;
import com.yinweilong.support.Tools;
import com.yinweilong.support.enums.AuthType;
import com.yinweilong.support.enums.UserType;

/**
 * 用戶身份拦截器
 * 
 * @author yin.weilong
 *
 */
@Service
public class UserAccessApiInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthRepository authRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		AccessRequired annotation = method.getAnnotation(AccessRequired.class);
		if (annotation != null) {
			BaseJson bj = new BaseJson();
			bj.setSuccess(0);
			bj.setMsg("权限不足");
			Cookie[] cookies = request.getCookies();
			String accessToken = null;
			for (int i = 0; cookies != null && i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				String name = cookie.getName();
				if (name.equals("accessToken")) {
					accessToken = cookie.getValue();
					User user = userRepository.findByAccessToken(accessToken);
					if (user == null) {
						bj.setSuccess(-1);
						bj.setMsg("请先登录");
						// 判断用户权限
						response.getWriter().write(Tools.caseObjectToJson(bj));
						return false;
					} else {
						if (handlerMethod.getBean().getClass().getSimpleName().equals("AccountRest") && (method.getName().equals("userAuth") || method.getName().equals("logout"))) {
							// 无需验证，直接放行
							response.setStatus(200);
							return true;
						} else {
							Auth auth = authRepository.findByClassNameAndMethodName(handlerMethod.getBean().getClass().getSimpleName(), method.getName());
							if (user.getType().equals(UserType.ROOT.name())) {
								if (!(auth.getType().equals(AuthType.ROOT_MENU.name()) || auth.getType().equals(AuthType.ROOT_ACTION.name()))) {
									response.getWriter().write(Tools.caseObjectToJson(bj));
									return false;
								}
							} else if (user.getType().equals(UserType.ADMIN.name())) {
								if (auth.getType().equals(AuthType.ROOT_MENU.name()) || auth.getType().equals(AuthType.ROOT_ACTION.name())) {
									response.getWriter().write(Tools.caseObjectToJson(bj));
									return false;
								}
							} else {
								Role role = roleRepository.findOne(user.getRoleId());
								List<Auth> auths = (List<Auth>) authRepository.findAll(role.getAuthIds());
								if (!auths.contains(auth)) {
									response.getWriter().write(Tools.caseObjectToJson(bj));
									return false;
								}
							}
						}
						return true;
					}
				}
			}
			response.setStatus(401);
			request.getRequestDispatcher("/401.html").forward(request, response);
			return false;
		}
		return true;
	}

}

package com.yinweilong.interceptor;

import java.lang.reflect.Method;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yinweilong.entity.User;
import com.yinweilong.json.BaseJson;
import com.yinweilong.repository.UserRepository;
import com.yinweilong.support.Tools;

/**
 * 用戶身份
 * 
 * @author yin.weilong
 *
 */
@Service
public class UserAccessApiInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private UserRepository userRepository;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		System.out.println(request.getServletPath());
		Method method = handlerMethod.getMethod();
		AccessRequired annotation = method.getAnnotation(AccessRequired.class);
		if (annotation != null) {
			BaseJson bj = new BaseJson();
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
						//对user的权限做认证
						response.setStatus(401);
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

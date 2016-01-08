package com.yinweilong.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yinweilong.support.Tools;

/**
 * 安全拦截器，利用用户的IP，防止用户无限恶意请求服务器
 * 
 * @author yin.weilong
 *
 */
public class SafetyInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String ip = Tools.getIpAddress(request);
		return true;
	}


}

package com.yinweilong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.yinweilong.interceptor.SafetyInterceptor;
import com.yinweilong.interceptor.UserAccessApiInterceptor;
import com.yinweilong.repository.UserRepository;

/**
 * 函数主入口
 * 
 * @author yin.weilong
 *
 */
@SpringBootApplication
public class InterfaceManageApplication extends WebMvcConfigurerAdapter implements EmbeddedServletContainerCustomizer {

	@Autowired
	private UserAccessApiInterceptor userAccessApiInterceptor;

	public static void main(String[] args) {
		SpringApplication.run(InterfaceManageApplication.class, args);
	}

	/**
	 * 配置服务器run的端口号
	 */
	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		container.setPort(8081);
	}

	/**
	 * 配置拦截器
	 * 
	 * @param registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SafetyInterceptor()).addPathPatterns("/**");
		registry.addInterceptor(userAccessApiInterceptor).addPathPatterns("/**");
	}
}

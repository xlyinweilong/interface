package com.yinweilong;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = InterfaceManageApplication.class)
@WebAppConfiguration
public class InterfaceManageApplicationTests {
	
	public static final String baseUrl = "http://localhost:8080";
	

	@Test
	public void contextLoads() {
		
	}

}

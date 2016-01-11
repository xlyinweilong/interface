package com.yinweilong.rest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.yinweilong.entity.Interface;
import com.yinweilong.entity.Project;
import com.yinweilong.interceptor.AccessRequired;
import com.yinweilong.interceptor.SerializedField;
import com.yinweilong.json.BaseJson;
import com.yinweilong.repository.*;
import com.yinweilong.support.AcceptHeaderHttpRequestInterceptor;
import com.yinweilong.support.Tools;
import com.yinweilong.support.entity.InterfaceParameter;
import com.yinweilong.support.enums.InterfaceConsumption;
import com.yinweilong.support.enums.InterfaceMethodType;

/**
 * REST接口接口
 * 
 * @author yin.weilong
 *
 */
@RestController
@RequestMapping(value = "/interface")
public class InterfaceRest {

	@Autowired
	private InterfaceRepository interfaceRepository;
	@Autowired
	private ProjectRepository projectRepository;

	/**
	 * 列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@SerializedField(includes = { "id", "createDate", "name", "projectId", "url", "methodType", "consumption", "parameters", "returnJson" })
	public BaseJson list(@CookieValue(value = "projectId", required = false) String projectId, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		return Tools.isBlank(projectId) ? new BaseJson(1, interfaceRepository.findAll(new PageRequest(pageNumber - 1, pageSize))) : new BaseJson(1, interfaceRepository.findByProjectId(projectId, new PageRequest(pageNumber - 1, pageSize)));
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/delete", method = RequestMethod.PUT)
	public BaseJson deleted(@RequestBody String[] ids) {
		interfaceRepository.deleteByIdIn(ids);
		return new BaseJson(1);
	}

	/**
	 * 查看某个
	 * 
	 * @param id
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
	@SerializedField(includes = { "id", "createDate", "name", "projectId", "url", "methodType", "consumption", "parameters", "returnJson" })
	public BaseJson info(@PathVariable String id) {
		return new BaseJson(1, interfaceRepository.findOne(id));
	}

	/**
	 * 创建
	 * 
	 * @param user
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@SerializedField(includes = { "id", "createDate", "name", "projectId", "url", "methodType", "consumption", "parameters", "returnJson" })
	public BaseJson create(@RequestBody Interface i) {
		return new BaseJson(1, "创建成功", interfaceRepository.insert(i));
	}

	/**
	 * 修改
	 * 
	 * @param user
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@SerializedField(includes = { "id", "createDate", "name", "projectId", "url", "methodType", "consumption", "parameters", "returnJson" })
	public BaseJson update(@RequestBody Interface i) {
		return new BaseJson(1, "修改成功", interfaceRepository.save(i));
	}

	/**
	 * 测试接口
	 * 
	 * @param user
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/test/{id}", method = RequestMethod.POST)
	public BaseJson test(@PathVariable String id) {
		Interface inter = interfaceRepository.findOne(id);
		RestTemplate restTemplate = new RestTemplate();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		if (inter.getMethodType().equals(InterfaceMethodType.POST.name()) || inter.getMethodType().equals(InterfaceMethodType.PUT.name())) {
			ClientHttpRequestInterceptor acceptHeader = new AcceptHeaderHttpRequestInterceptor(InterfaceConsumption.valueOf(inter.getConsumption()).getMean());
			List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
			interceptors.add(acceptHeader);
			restTemplate.setInterceptors(interceptors);
		}
		Project project = projectRepository.findOne(inter.getProjectId());
		if (inter.getMethodType().equals(InterfaceMethodType.GET.name())) {
			if (inter.getParameters() != null && !inter.getParameters().isEmpty()) {
				Map<String, String> urlVariables = new HashMap<>();
				for (InterfaceParameter ip : inter.getParameters()) {
					urlVariables.put(ip.getName(), ip.getValue());
				}
			} else {
			}
			try {
				HttpGet httpGet = new HttpGet(project.getBaseUrlDemo() + inter.getUrl());
				System.out.println("Executing request: " + httpGet.getRequestLine());
				CloseableHttpResponse response = httpclient.execute(httpGet);
				try {
					System.out.println("----------------------------------------");
					System.out.println(response.getStatusLine());
					System.out.println(EntityUtils.toString(response.getEntity()));
				} finally {
					response.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (inter.getMethodType().equals(InterfaceMethodType.POST.name())) {
			try {
				HttpUriRequest login = RequestBuilder.post().setUri(new URI("https://someportal/")).addParameter("IDToken1", "username").addParameter("IDToken2", "password").build();
				CloseableHttpResponse response2 = httpclient.execute(login);
				HttpPost httppost = new HttpPost("http://httpbin.org/post");
				System.out.println("Executing request: " + httppost.getRequestLine());
				CloseableHttpResponse response = httpclient.execute(httppost);
				try {
					System.out.println("----------------------------------------");
					System.out.println(response.getStatusLine());
					System.out.println(EntityUtils.toString(response.getEntity()));
				} finally {
					response.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (inter.getMethodType().equals(InterfaceMethodType.PUT.name())) {
			Object request = new Object();
			if (inter.getParameters() != null && !inter.getParameters().isEmpty()) {
				Map<String, String> urlVariables = new HashMap<>();
				for (InterfaceParameter ip : inter.getParameters()) {
					urlVariables.put(ip.getName(), ip.getValue());
				}
				restTemplate.put(project.getBaseUrlDemo() + inter.getUrl(), request, Object.class, urlVariables);
			} else {
				restTemplate.put(project.getBaseUrlDemo() + inter.getUrl(), request, Object.class);
			}
		} else if (inter.getMethodType().equals(InterfaceMethodType.DELETE.name())) {
			if (inter.getParameters() != null && !inter.getParameters().isEmpty()) {
				Map<String, String> urlVariables = new HashMap<>();
				for (InterfaceParameter ip : inter.getParameters()) {
					urlVariables.put(ip.getName(), ip.getValue());
				}
				restTemplate.delete(project.getBaseUrlDemo() + inter.getUrl(), urlVariables);
			} else {
				restTemplate.delete(project.getBaseUrlDemo() + inter.getUrl());
			}

		} else {
			return new BaseJson(0, "暂时还没有这个请求方式", null);
		}
		return new BaseJson(1);
	}

}

package com.yinweilong.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.yinweilong.support.entity.InterfaceParameter;
import com.yinweilong.support.enums.InterfaceConsumption;
import com.yinweilong.support.enums.InterfaceMethodType;

/**
 * 接口
 * 
 * @author yin.weilong
 *
 */
@Document(collection = "interface")
public class Interface {

	@Id
	// 数据库生成的ID
	private String id;
	// 数据生成时间
	@Indexed(unique = false)
	private Date createDate = new Date();
	// 接口名称
	private String name;
	// 项目ID
	private String projectId;
	
	// 消费格式
	private String consumption = InterfaceConsumption.APPLICATION_FORM_URLENCODED.name(); 

	// 方法类型
	private String methodType = InterfaceMethodType.GET.name();

	// 资源URL
	private String url;

	// 传入参数
	private List<InterfaceParameter> parameters;

	// 输出的JSON
	private String returnJson;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getMethodType() {
		return methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getReturnJson() {
		return returnJson;
	}

	public void setReturnJson(String returnJson) {
		this.returnJson = returnJson;
	}

	public List<InterfaceParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<InterfaceParameter> parameters) {
		this.parameters = parameters;
	}

	public String getConsumption() {
		return consumption;
	}

	public void setConsumption(String consumption) {
		this.consumption = consumption;
	}

}

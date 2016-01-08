package com.yinweilong.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 项目
 * 
 * @author yin.weilong
 *
 */
@Document(collection = "project")
public class Project {

	@Id
	// 数据库生成的ID
	private String id;
	// 数据生成时间
	@Indexed(unique = false)
	private Date createDate = new Date();
	// 项目名称
	private String name;

	// 项目根目录
	private String baseUrl;

	// 测试URL
	private String baseUrlDemo;

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

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBaseUrlDemo() {
		return baseUrlDemo;
	}

	public void setBaseUrlDemo(String baseUrlDemo) {
		this.baseUrlDemo = baseUrlDemo;
	}

}

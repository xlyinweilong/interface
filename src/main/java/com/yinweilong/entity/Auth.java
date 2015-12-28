package com.yinweilong.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;

/**
 * 权限
 * 
 * @author yin.weilong
 *
 */
public class Auth {

	@Id
	// 数据库生成的ID
	private String id;
	// 数据生成时间
	private Date createDate = new Date();
	// 权限姓名
	private String name;
	// 类名
	private String className;
	// 方法名
	private String methodName;

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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

}

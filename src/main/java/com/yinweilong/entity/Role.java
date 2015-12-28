package com.yinweilong.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

/**
 * 角色
 * 
 * @author yin.weilong
 *
 */
public class Role {

	@Id
	// 数据库生成的ID
	private String id;
	// 数据生成时间
	private Date createDate = new Date();
	// 角色姓名
	private String name;
	// 角色的权限
	private List<String> authNames; 
	

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

	public List<String> getAuthNames() {
		return authNames;
	}

	public void setAuthNames(List<String> authNames) {
		this.authNames = authNames;
	}

}

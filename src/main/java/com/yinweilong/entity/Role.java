package com.yinweilong.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 角色
 * 
 * @author yin.weilong
 *
 */
@Document(collection = "role")
public class Role {

	@Id
	// 数据库生成的ID
	private String id;
	// 数据生成时间
	@Indexed(unique = false)
	private Date createDate = new Date();
	// 角色姓名
	private String name;
	// 角色的权限
	private List<String> authIds;
	

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

	public List<String> getAuthIds() {
		return authIds;
	}

	public void setAuthIds(List<String> authIds) {
		this.authIds = authIds;
	}

}

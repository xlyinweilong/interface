package com.yinweilong.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.yinweilong.support.enums.UserType;

/**
 * 用户
 * 
 * @author yin.weilong
 *
 */
@Document(collection = "user")
public class User {

	@Id
	// 数据库生成的ID
	private String id;
	// 数据生成时间
	@Indexed(unique = false)
	private Date createDate = new Date();
	// 用户账号
	@Indexed(unique = true)
	private String account;
	// 用户姓名
	private String name;
	// 用户密码
	private String passwd;
	// 用户的accessToken
	@Indexed(unique = true)
	private String accessToken;
	// 用户密钥
	private String security;
	// 用户角色
	private String roleId;
	// 用户类型
	private final String type = UserType.USER.name();
	// 删除标记
	private boolean deleted = false;

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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getType() {
		return type;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}

package com.yinweilong.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.yinweilong.support.enums.AuthType;

/**
 * 权限
 * 
 * @author yin.weilong
 *
 */
@Document(collection = "auth")
@CompoundIndexes({ @CompoundIndex(name = "class_method_inx", def = "{'className': 1, 'methodName': 1}") })
public class Auth {

	@Id
	// 数据库生成的ID
	private String id;
	// 数据生成时间
	@Indexed(unique = false)
	private Date createDate = new Date();
	// 权限姓名
	private String name;
	// 类名
	private String className;
	// 方法名
	private String methodName;
	// 类型
	private String type = AuthType.USER_MENU.name();
	// url
	private String url;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

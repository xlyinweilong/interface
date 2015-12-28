package com.yinweilong.json;

public class BaseJson {
	private int success = 0;
	private String msg;
	private Object data;

	public BaseJson() {
		super();
	}

	public BaseJson(int success, Object data) {
		super();
		this.success = success;
		this.data = data;
	}

	public BaseJson(int success, String msg, Object data) {
		super();
		this.success = success;
		this.msg = msg;
		this.data = data;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}

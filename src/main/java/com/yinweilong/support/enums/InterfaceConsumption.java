package com.yinweilong.support.enums;

public enum InterfaceConsumption {
	APPLICATION_FORM_URLENCODED, APPLICATION_JSON, MULTIPART_FORM_DATA;

	public String getMean() {
		switch (this) {
		case APPLICATION_JSON:
			return "application/json";
		case APPLICATION_FORM_URLENCODED:
			return "application/x-www-form-urlencoded";
		case MULTIPART_FORM_DATA:
			return "multipart/form-data";
		}
		return null;
	}
}

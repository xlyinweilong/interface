package com.yinweilong.support;

import java.util.List;

public class ReturnPage {
	private List<Object> content;
	private List<Object> sort = null;
	private int totalPages = 0;
	private long totalElements = 0l;
	private int size = 0;

	public ReturnPage(List<Object> content, int totalPages, long totalElements, int size) {
		super();
		this.content = content;
		this.totalPages = totalPages;
		this.totalElements = totalElements;
		this.size = size;
	}

	public ReturnPage(List<Object> content, List<Object> sort, int totalPages, long totalElements, int size) {
		super();
		this.content = content;
		this.sort = sort;
		this.totalPages = totalPages;
		this.totalElements = totalElements;
		this.size = size;
	}

	public List<Object> getContent() {
		return content;
	}

	public void setContent(List<Object> content) {
		this.content = content;
	}

	public List<Object> getSort() {
		return sort;
	}

	public void setSort(List<Object> sort) {
		this.sort = sort;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}

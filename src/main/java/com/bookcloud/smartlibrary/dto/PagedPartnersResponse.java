package com.bookcloud.smartlibrary.dto;

import java.util.List;

import com.bookcloud.smartlibrary.model.Partner;

public class PagedPartnersResponse {
	private List<Partner> content;
	private long totalElements;
	private int totalPages;
	private int number;
	private int size;

	public PagedPartnersResponse() {
	}

	public PagedPartnersResponse(List<Partner> content, long totalElements, int totalPages, int number, int size) {
		this.content = content;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.number = number;
		this.size = size;
	}

	public List<Partner> getContent() {
		return content;
	}

	public void setContent(List<Partner> content) {
		this.content = content;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}


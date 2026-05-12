package com.bookcloud.smartlibrary.dto;

import com.bookcloud.smartlibrary.enums.OfferType;

public class OfferAdminQuery {
	private int page = 0;
	private int size = 12;
	private String search;
	private OfferType type;
	private Boolean active;
	private Boolean expired;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public OfferType getType() {
		return type;
	}

	public void setType(OfferType type) {
		this.type = type;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}
}


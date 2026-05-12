package com.bookcloud.smartlibrary.dto;

import org.springframework.web.multipart.MultipartFile;

public class UpdateAuthorRequest {

	private String name;
	private String bio;
	private String country;
	private MultipartFile image;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}
}

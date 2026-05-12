package com.bookcloud.smartlibrary.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryProperties {
	private boolean enabled = false;
	private String cloudName = "";
	private String apiKey = "";
	private String apiSecret = "";
	private String folder = "bookcloud/partners";
	/** Dossier Cloudinary pour les visuels de catégories (home + admin). */
	private String categoryFolder = "bookcloud/categories";
	/** Dossier Cloudinary pour les couvertures de livres. */
	private String bookFolder = "bookcloud/books";
	/** Dossier Cloudinary pour les avatars d’auteurs. */
	private String authorFolder = "bookcloud/authors";
	/** Dossier Cloudinary pour les visuels de news (Latest News). */
	private String newsFolder = "bookcloud/news";

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getCloudName() {
		return cloudName;
	}

	public void setCloudName(String cloudName) {
		this.cloudName = cloudName;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiSecret() {
		return apiSecret;
	}

	public void setApiSecret(String apiSecret) {
		this.apiSecret = apiSecret;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getCategoryFolder() {
		return categoryFolder;
	}

	public void setCategoryFolder(String categoryFolder) {
		this.categoryFolder = categoryFolder;
	}

	public String getBookFolder() {
		return bookFolder;
	}

	public void setBookFolder(String bookFolder) {
		this.bookFolder = bookFolder;
	}

	public String getAuthorFolder() {
		return authorFolder;
	}

	public void setAuthorFolder(String authorFolder) {
		this.authorFolder = authorFolder;
	}

	public String getNewsFolder() {
		return newsFolder;
	}

	public void setNewsFolder(String newsFolder) {
		this.newsFolder = newsFolder;
	}
}


package com.bookcloud.smartlibrary.serviceimpl;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bookcloud.smartlibrary.config.CloudinaryProperties;
import com.bookcloud.smartlibrary.exception.BusinessRuleException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryImageService {

	private final CloudinaryProperties cloudinaryProperties;
	private final Cloudinary cloudinary;

	public CloudinaryImageService(
			CloudinaryProperties cloudinaryProperties,
			Cloudinary cloudinary) {
		this.cloudinaryProperties = cloudinaryProperties;
		this.cloudinary = cloudinary;
	}

	public String upload(MultipartFile file, String cloudFolder, String prefix, Long entityId) {
		if (file == null || file.isEmpty()) {
			throw new BusinessRuleException("Image file is required");
		}
		if (!isCloudinaryConfigured()) {
			throw new BusinessRuleException("Cloudinary is not configured");
		}

		String extension = inferExtension(file.getContentType(), file.getOriginalFilename());
		if (extension == null) {
			throw new BusinessRuleException("Unsupported image format");
		}

		String fingerprint = hash(file);
		String publicId = prefix + "-" + entityId + "-" + fingerprint;

		try {
			Map<?, ?> result = cloudinary.uploader().upload(
					file.getBytes(),
					ObjectUtils.asMap(
							"folder", normalizeFolder(cloudFolder),
							"public_id", publicId,
							"overwrite", true,
							"resource_type", "image"));
			Object secureUrl = result.get("secure_url");
			if (secureUrl instanceof String url && StringUtils.hasText(url)) {
				return url;
			}
			throw new BusinessRuleException("Cloudinary upload failed");
		} catch (Exception ex) {
			throw new BusinessRuleException("Failed to upload image to Cloudinary");
		}
	}

	private boolean isCloudinaryConfigured() {
		return cloudinaryProperties.isEnabled()
				&& StringUtils.hasText(cloudinaryProperties.getCloudName())
				&& StringUtils.hasText(cloudinaryProperties.getApiKey())
				&& StringUtils.hasText(cloudinaryProperties.getApiSecret());
	}

	private String inferExtension(String contentType, String originalFilename) {
		String type = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
		if (type.contains("png")) {
			return ".png";
		}
		if (type.contains("jpeg") || type.contains("jpg")) {
			return ".jpg";
		}
		if (type.contains("webp")) {
			return ".webp";
		}
		if (type.contains("svg")) {
			return ".svg";
		}
		String filename = originalFilename == null ? "" : originalFilename.toLowerCase(Locale.ROOT);
		if (filename.endsWith(".png")) {
			return ".png";
		}
		if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
			return ".jpg";
		}
		if (filename.endsWith(".webp")) {
			return ".webp";
		}
		if (filename.endsWith(".svg")) {
			return ".svg";
		}
		return null;
	}

	private String hash(MultipartFile file) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return HexFormat.of().formatHex(digest.digest(file.getBytes())).substring(0, 16);
		} catch (Exception ex) {
			throw new BusinessRuleException("Failed to hash image");
		}
	}

	private String normalizeFolder(String folder) {
		if (folder == null) {
			return "";
		}
		String value = folder.trim();
		while (value.startsWith("/")) {
			value = value.substring(1);
		}
		return value;
	}
}

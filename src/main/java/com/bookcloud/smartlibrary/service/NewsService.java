package com.bookcloud.smartlibrary.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bookcloud.smartlibrary.dto.CreateNewsRequest;
import com.bookcloud.smartlibrary.dto.NewsDto;
import com.bookcloud.smartlibrary.dto.UpdateNewsRequest;
import com.bookcloud.smartlibrary.enums.NewsType;

public interface NewsService {
	List<NewsDto> listActive(NewsType type);
	List<NewsDto> listAdmin(Boolean active, NewsType type);
	NewsDto getPublic(Long id);
	NewsDto getAdmin(Long id);
	Long create(CreateNewsRequest req);
	NewsDto update(Long id, UpdateNewsRequest req);
	void delete(Long id);
	NewsDto uploadImage(Long id, MultipartFile file);
}

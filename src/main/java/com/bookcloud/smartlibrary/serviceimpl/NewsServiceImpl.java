package com.bookcloud.smartlibrary.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bookcloud.smartlibrary.config.CloudinaryProperties;
import com.bookcloud.smartlibrary.dto.CreateNewsRequest;
import com.bookcloud.smartlibrary.dto.NewsDto;
import com.bookcloud.smartlibrary.dto.UpdateNewsRequest;
import com.bookcloud.smartlibrary.enums.NewsType;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.News;
import com.bookcloud.smartlibrary.repository.NewsRepository;
import com.bookcloud.smartlibrary.service.NewsService;

@Service
@Transactional
public class NewsServiceImpl implements NewsService {

	private final NewsRepository newsRepository;
	private final CloudinaryImageService cloudinaryImageService;
	private final CloudinaryProperties cloudinaryProperties;

	public NewsServiceImpl(
			NewsRepository newsRepository,
			CloudinaryImageService cloudinaryImageService,
			CloudinaryProperties cloudinaryProperties) {
		this.newsRepository = newsRepository;
		this.cloudinaryImageService = cloudinaryImageService;
		this.cloudinaryProperties = cloudinaryProperties;
	}

	@Override
	@Transactional(readOnly = true)
	public List<NewsDto> listActive(NewsType type) {
		List<News> news = type == null
				? newsRepository.findByActiveTrueOrderByCreatedAtDesc()
				: newsRepository.findByActiveTrueAndTypeOrderByCreatedAtDesc(type);
		return news.stream().map(this::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<NewsDto> listAdmin(Boolean active, NewsType type) {
		List<News> news;
		if (active == null) {
			news = newsRepository.findAllByOrderByCreatedAtDesc();
		} else if (type == null) {
			news = newsRepository.findByActiveOrderByCreatedAtDesc(active);
		} else {
			news = newsRepository.findByActiveAndTypeOrderByCreatedAtDesc(active, type);
		}
		if (active == null && type != null) {
			news = news.stream().filter(item -> item.getType() == type).toList();
		}
		return news.stream().map(this::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public NewsDto getPublic(Long id) {
		News news = getEntity(id);
		if (!news.isActive()) {
			throw new ResourceNotFoundException("News item not found");
		}
		return toDto(news);
	}

	@Override
	@Transactional(readOnly = true)
	public NewsDto getAdmin(Long id) {
		return toDto(getEntity(id));
	}

	@Override
	public Long create(CreateNewsRequest req) {
		News news = new News();
		news.setTitle(req.getTitle());
		news.setContent(req.getContent());
		news.setType(req.getType());
		news.setImageUrl(req.getImageUrl());
		news.setActive(req.isActive());
		return newsRepository.save(news).getId();
	}

	@Override
	public NewsDto update(Long id, UpdateNewsRequest req) {
		News news = getEntity(id);
		if (req.getTitle() != null) {
			news.setTitle(req.getTitle());
		}
		if (req.getContent() != null) {
			news.setContent(req.getContent());
		}
		if (req.getType() != null) {
			news.setType(req.getType());
		}
		if (req.getImageUrl() != null) {
			news.setImageUrl(req.getImageUrl());
		}
		if (req.getActive() != null) {
			news.setActive(req.getActive());
		}
		return toDto(newsRepository.save(news));
	}

	@Override
	public void delete(Long id) {
		newsRepository.delete(getEntity(id));
	}

	@Override
	public NewsDto uploadImage(Long id, MultipartFile file) {
		News news = getEntity(id);
		String url = cloudinaryImageService.upload(file, cloudinaryProperties.getNewsFolder(), "news", id);
		news.setImageUrl(url);
		return toDto(newsRepository.save(news));
	}

	private News getEntity(Long id) {
		return newsRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("News item not found"));
	}

	private NewsDto toDto(News news) {
		NewsDto dto = new NewsDto();
		dto.setId(news.getId());
		dto.setTitle(news.getTitle());
		dto.setContent(news.getContent());
		dto.setType(news.getType());
		dto.setImageUrl(news.getImageUrl());
		dto.setCreatedAt(news.getCreatedAt());
		dto.setActive(news.isActive());
		return dto;
	}
}

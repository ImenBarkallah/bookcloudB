package com.bookcloud.smartlibrary.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bookcloud.smartlibrary.config.CloudinaryProperties;
import com.bookcloud.smartlibrary.dto.CreateAuthorRequest;
import com.bookcloud.smartlibrary.dto.UpdateAuthorRequest;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.Author;
import com.bookcloud.smartlibrary.repository.AuthorRepository;
import com.bookcloud.smartlibrary.service.AuthorService;

@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

	private final AuthorRepository authorRepository;
	private final CloudinaryImageService cloudinaryImageService;
	private final CloudinaryProperties cloudinaryProperties;

	public AuthorServiceImpl(
			AuthorRepository authorRepository,
			CloudinaryImageService cloudinaryImageService,
			CloudinaryProperties cloudinaryProperties) {
		this.authorRepository = authorRepository;
		this.cloudinaryImageService = cloudinaryImageService;
		this.cloudinaryProperties = cloudinaryProperties;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Author> listAll() {
		return authorRepository.findAllByOrderByNameAsc();
	}

	@Override
	@Transactional(readOnly = true)
	public Author getById(Long id) {
		return authorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Author not found"));
	}

	@Override
	public Long create(CreateAuthorRequest req) {
		Author author = new Author();
		author.setName(req.getName());
		author.setBio(req.getBio());
		author.setCountry(req.getCountry());
		author = authorRepository.save(author);
		applyImage(author, req.getImage());
		return author.getId();
	}

	@Override
	public void update(Long id, UpdateAuthorRequest req) {
		Author author = getById(id);
		if (req.getName() != null) {
			author.setName(req.getName());
		}
		if (req.getBio() != null) {
			author.setBio(req.getBio());
		}
		if (req.getCountry() != null) {
			author.setCountry(req.getCountry());
		}
		applyImage(author, req.getImage());
		authorRepository.save(author);
	}

	@Override
	public void delete(Long id) {
		authorRepository.delete(getById(id));
	}

	private void applyImage(Author author, MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return;
		}
		String url = cloudinaryImageService.upload(
				file,
				cloudinaryProperties.getAuthorFolder(),
				"author",
				author.getId());
		author.setImageUrl(url);
		authorRepository.save(author);
	}
}

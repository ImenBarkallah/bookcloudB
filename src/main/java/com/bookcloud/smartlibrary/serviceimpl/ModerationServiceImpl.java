package com.bookcloud.smartlibrary.serviceimpl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.dto.CreateModerationReportRequest;
import com.bookcloud.smartlibrary.dto.ResolveModerationReportRequest;
import com.bookcloud.smartlibrary.enums.ModerationReportStatus;
import com.bookcloud.smartlibrary.exception.BusinessRuleException;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.Author;
import com.bookcloud.smartlibrary.model.Book;
import com.bookcloud.smartlibrary.model.ModerationReport;
import com.bookcloud.smartlibrary.repository.AuthorRepository;
import com.bookcloud.smartlibrary.repository.BookRepository;
import com.bookcloud.smartlibrary.repository.ModerationReportRepository;
import com.bookcloud.smartlibrary.service.ModerationService;

@Service
@Transactional
public class ModerationServiceImpl implements ModerationService {

	private final ModerationReportRepository repository;
	private final BookRepository bookRepository;
	private final AuthorRepository authorRepository;

	public ModerationServiceImpl(
			ModerationReportRepository repository,
			BookRepository bookRepository,
			AuthorRepository authorRepository) {
		this.repository = repository;
		this.bookRepository = bookRepository;
		this.authorRepository = authorRepository;
	}

	@Override
	public Long report(CreateModerationReportRequest req, String reporterUid) {
		ModerationReport report = new ModerationReport();
		report.setEntityType(req.getEntityType());
		report.setEntityId(req.getEntityId());
		report.setReason(req.getReason());
		report.setDetails(req.getDetails());
		report.setReporterUid(reporterUid);
		report.setStatus(ModerationReportStatus.OPEN);
		return repository.save(report).getId();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ModerationReport> list(String status) {
		if (status == null || status.isBlank()) {
			return repository.findAllByOrderByCreatedAtDesc();
		}
		return repository.findByStatusOrderByCreatedAtDesc(ModerationReportStatus.valueOf(status.trim().toUpperCase()));
	}

	@Override
	public ModerationReport resolve(Long reportId, ResolveModerationReportRequest req, String actingUid) {
		ModerationReport report = repository.findById(reportId)
				.orElseThrow(() -> new ResourceNotFoundException("Moderation report not found"));
		report.setResolvedAt(Instant.now());
		report.setResolvedByUid(actingUid);
		report.setResolutionNote(req.getNote());
		boolean hide = Boolean.TRUE.equals(req.getHideContent());
		if ("RESOLVE".equalsIgnoreCase(req.getAction())) {
			report.setStatus(ModerationReportStatus.RESOLVED);
		} else {
			report.setStatus(ModerationReportStatus.REJECTED);
		}
		if (hide && report.getEntityId() != null) {
			setHidden(report.getEntityType(), report.getEntityId(), true);
		}
		return repository.save(report);
	}

	@Override
	public void setHidden(String entityType, Long entityId, boolean hidden) {
		if ("BOOK".equalsIgnoreCase(entityType)) {
			Book book = bookRepository.findById(entityId)
					.orElseThrow(() -> new ResourceNotFoundException("Book not found"));
			book.setHidden(hidden);
			bookRepository.save(book);
			return;
		}
		if ("AUTHOR".equalsIgnoreCase(entityType)) {
			Author author = authorRepository.findById(entityId)
					.orElseThrow(() -> new ResourceNotFoundException("Author not found"));
			author.setHidden(hidden);
			authorRepository.save(author);
			return;
		}
		throw new BusinessRuleException("Unsupported moderation target");
	}
}

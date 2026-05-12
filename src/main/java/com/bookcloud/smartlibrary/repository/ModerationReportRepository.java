package com.bookcloud.smartlibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookcloud.smartlibrary.enums.ModerationReportStatus;
import com.bookcloud.smartlibrary.model.ModerationReport;

public interface ModerationReportRepository extends JpaRepository<ModerationReport, Long> {

	List<ModerationReport> findByStatusOrderByCreatedAtDesc(ModerationReportStatus status);

	List<ModerationReport> findAllByOrderByCreatedAtDesc();
}

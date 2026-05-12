package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.CreateModerationReportRequest;
import com.bookcloud.smartlibrary.dto.ResolveModerationReportRequest;
import com.bookcloud.smartlibrary.model.ModerationReport;

public interface ModerationService {
	Long report(CreateModerationReportRequest req, String reporterUid);
	List<ModerationReport> list(String status);
	ModerationReport resolve(Long reportId, ResolveModerationReportRequest req, String actingUid);
	void setHidden(String entityType, Long entityId, boolean hidden);
}

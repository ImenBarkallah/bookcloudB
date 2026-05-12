package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.CreatePartnerRequest;
import com.bookcloud.smartlibrary.dto.PagedPartnersResponse;
import com.bookcloud.smartlibrary.dto.UpdatePartnerRequest;
import com.bookcloud.smartlibrary.model.Partner;

public interface PartnerService {
	List<Partner> listPublic();
	List<Partner> listAdmin();
	PagedPartnersResponse listAdminPaged(int page, int size, String search, Boolean includeArchived);
	Partner get(Long id);
	Long create(CreatePartnerRequest req);
	void update(Long id, UpdatePartnerRequest req);
	void setArchived(Long id, boolean archived);
	void delete(Long id);
}

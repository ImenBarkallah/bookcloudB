package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.CreateOfferRequest;
import com.bookcloud.smartlibrary.dto.OfferAdminQuery;
import com.bookcloud.smartlibrary.dto.OfferDto;
import com.bookcloud.smartlibrary.model.Offer;

public interface OfferService {
	List<OfferDto> listAllAdmin(OfferAdminQuery q);
	OfferDto getByIdForUser(Long id);
	OfferDto getByIdForAdmin(Long id);
	Long create(CreateOfferRequest req);
	Offer update(Long id, CreateOfferRequest req);
	void delete(Long id);
	List<OfferDto> listActiveForUsers();
	List<OfferDto> recommendedForUser(String userUid, int limit);
}

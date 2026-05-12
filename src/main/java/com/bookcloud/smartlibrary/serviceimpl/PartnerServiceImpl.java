package com.bookcloud.smartlibrary.serviceimpl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bookcloud.smartlibrary.config.CloudinaryProperties;
import com.bookcloud.smartlibrary.dto.CreatePartnerRequest;
import com.bookcloud.smartlibrary.dto.PagedPartnersResponse;
import com.bookcloud.smartlibrary.dto.UpdatePartnerRequest;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.Partner;
import com.bookcloud.smartlibrary.repository.PartnerRepository;
import com.bookcloud.smartlibrary.service.PartnerService;

@Service
@Transactional
public class PartnerServiceImpl implements PartnerService {

	private final PartnerRepository partnerRepository;
	private final CloudinaryImageService cloudinaryImageService;
	private final CloudinaryProperties cloudinaryProperties;

	public PartnerServiceImpl(
			PartnerRepository partnerRepository,
			CloudinaryImageService cloudinaryImageService,
			CloudinaryProperties cloudinaryProperties) {
		this.partnerRepository = partnerRepository;
		this.cloudinaryImageService = cloudinaryImageService;
		this.cloudinaryProperties = cloudinaryProperties;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Partner> listPublic() {
		return partnerRepository.findByArchivedFalseOrderByNameAsc();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Partner> listAdmin() {
		return partnerRepository.findAllByOrderByNameAsc();
	}

	@Override
	@Transactional(readOnly = true)
	public PagedPartnersResponse listAdminPaged(int page, int size, String search, Boolean includeArchived) {
		String s = (search == null || search.trim().isEmpty()) ? null : search.trim();
		var result = partnerRepository.search(s, Boolean.TRUE.equals(includeArchived), PageRequest.of(page, size));
		return new PagedPartnersResponse(result.getContent(), result.getTotalElements(), result.getTotalPages(),
				result.getNumber(), result.getSize());
	}

	@Override
	@Transactional(readOnly = true)
	public Partner get(Long id) {
		return partnerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Partner not found"));
	}

	@Override
	public Long create(CreatePartnerRequest req) {
		Partner partner = new Partner();
		partner.setName(req.getName());
		partner.setTier(req.getTier());
		partner.setWebsite(req.getWebsite());
		partner.setArchived(req.isArchived());
		partner = partnerRepository.save(partner);
		applyLogo(partner, req.getLogo());
		return partner.getId();
	}

	@Override
	public void update(Long id, UpdatePartnerRequest req) {
		Partner partner = get(id);
		if (req.getName() != null) {
			partner.setName(req.getName());
		}
		if (req.getTier() != null) {
			partner.setTier(req.getTier());
		}
		if (req.getWebsite() != null) {
			partner.setWebsite(req.getWebsite());
		}
		partner.setArchived(req.isArchived());
		applyLogo(partner, req.getLogo());
		partnerRepository.save(partner);
	}

	@Override
	public void setArchived(Long id, boolean archived) {
		Partner partner = get(id);
		partner.setArchived(archived);
		partnerRepository.save(partner);
	}

	@Override
	public void delete(Long id) {
		partnerRepository.delete(get(id));
	}

	private void applyLogo(Partner partner, MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return;
		}
		String url = cloudinaryImageService.upload(
				file,
				cloudinaryProperties.getFolder(),
				"partner",
				partner.getId());
		partner.setLogoUrl(url);
		partnerRepository.save(partner);
	}
}

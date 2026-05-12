package com.bookcloud.smartlibrary.serviceimpl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.enums.FineStatus;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.exception.BusinessRuleException;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.Fine;
import com.bookcloud.smartlibrary.repository.FineRepository;
import com.bookcloud.smartlibrary.service.FineService;

@Service
@Transactional
public class FineServiceImpl implements FineService {

	private final FineRepository fineRepository;

	public FineServiceImpl(FineRepository fineRepository) {
		this.fineRepository = fineRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Fine> listMine(String userUid) {
		return fineRepository.findByUser_UidOrderByCreatedAtDesc(userUid);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Fine> listAll(Role role) {
		if (role != Role.ADMIN && role != Role.LIBRARIAN) {
			throw new BusinessRuleException("Staff access required");
		}
		return fineRepository.findAll();
	}

	@Override
	public Fine markPaid(Long fineId, Role role) {
		if (role != Role.ADMIN && role != Role.LIBRARIAN) {
			throw new BusinessRuleException("Staff access required");
		}
		Fine fine = fineRepository.findById(fineId)
				.orElseThrow(() -> new ResourceNotFoundException("Fine not found"));
		fine.setStatus(FineStatus.PAID);
		fine.setPaidAt(Instant.now());
		return fineRepository.save(fine);
	}
}

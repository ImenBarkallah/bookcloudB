package com.bookcloud.smartlibrary.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.exception.BusinessRuleException;
import com.bookcloud.smartlibrary.model.TransactionRecord;
import com.bookcloud.smartlibrary.repository.TransactionRecordRepository;
import com.bookcloud.smartlibrary.service.TransactionRecordQueryService;

@Service
@Transactional(readOnly = true)
public class TransactionRecordQueryServiceImpl implements TransactionRecordQueryService {

	private final TransactionRecordRepository transactionRecordRepository;

	public TransactionRecordQueryServiceImpl(TransactionRecordRepository transactionRecordRepository) {
		this.transactionRecordRepository = transactionRecordRepository;
	}

	@Override
	public List<TransactionRecord> listForUser(String userUid) {
		return transactionRecordRepository.findByUser_UidOrderByOccurredAtDesc(userUid);
	}

	@Override
	public List<TransactionRecord> listAll(Role role) {
		if (role != Role.ADMIN && role != Role.LIBRARIAN) {
			throw new BusinessRuleException("Staff access required");
		}
		return transactionRecordRepository.findAllDetailed();
	}
}

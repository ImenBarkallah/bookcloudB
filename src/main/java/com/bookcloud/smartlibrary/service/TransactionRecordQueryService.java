package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.model.TransactionRecord;

public interface TransactionRecordQueryService {
	List<TransactionRecord> listForUser(String userUid);
	List<TransactionRecord> listAll(Role role);
}

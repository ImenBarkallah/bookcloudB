package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.model.Fine;

public interface FineService {
	List<Fine> listMine(String userUid);
	List<Fine> listAll(Role role);
	Fine markPaid(Long fineId, Role role);
}

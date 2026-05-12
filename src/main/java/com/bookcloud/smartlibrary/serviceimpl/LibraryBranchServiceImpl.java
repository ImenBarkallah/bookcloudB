package com.bookcloud.smartlibrary.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.dto.CreateLibraryBranchRequest;
import com.bookcloud.smartlibrary.dto.UpdateLibraryBranchRequest;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.exception.BusinessRuleException;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.LibraryBranch;
import com.bookcloud.smartlibrary.repository.LibraryBranchRepository;
import com.bookcloud.smartlibrary.service.LibraryBranchService;

@Service
@Transactional
public class LibraryBranchServiceImpl implements LibraryBranchService {

	private final LibraryBranchRepository libraryBranchRepository;

	public LibraryBranchServiceImpl(LibraryBranchRepository libraryBranchRepository) {
		this.libraryBranchRepository = libraryBranchRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<LibraryBranch> listAll() {
		return libraryBranchRepository.findAllByOrderByNameAsc();
	}

	@Override
	@Transactional(readOnly = true)
	public LibraryBranch getById(Long id) {
		return libraryBranchRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
	}

	@Override
	public LibraryBranch create(CreateLibraryBranchRequest req, Role role) {
		requireStaff(role);
		LibraryBranch branch = new LibraryBranch();
		branch.setName(req.getName());
		branch.setAddress(req.getAddress());
		branch.setOpeningHours(req.getOpeningHours());
		return libraryBranchRepository.save(branch);
	}

	@Override
	public LibraryBranch update(Long id, UpdateLibraryBranchRequest req, Role role) {
		requireStaff(role);
		LibraryBranch branch = getById(id);
		if (req.getName() != null) {
			branch.setName(req.getName());
		}
		if (req.getAddress() != null) {
			branch.setAddress(req.getAddress());
		}
		if (req.getOpeningHours() != null) {
			branch.setOpeningHours(req.getOpeningHours());
		}
		return libraryBranchRepository.save(branch);
	}

	@Override
	public void delete(Long id, Role role) {
		requireStaff(role);
		libraryBranchRepository.delete(getById(id));
	}

	private void requireStaff(Role role) {
		if (role != Role.ADMIN && role != Role.LIBRARIAN) {
			throw new BusinessRuleException("Staff access required");
		}
	}
}

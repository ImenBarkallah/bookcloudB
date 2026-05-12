package com.bookcloud.smartlibrary.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookcloud.smartlibrary.dto.CreateStaffAssignmentRequest;
import com.bookcloud.smartlibrary.dto.UpdateStaffAssignmentRequest;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.exception.BusinessRuleException;
import com.bookcloud.smartlibrary.exception.ResourceNotFoundException;
import com.bookcloud.smartlibrary.model.AppUser;
import com.bookcloud.smartlibrary.model.LibraryBranch;
import com.bookcloud.smartlibrary.model.StaffAssignment;
import com.bookcloud.smartlibrary.repository.LibraryBranchRepository;
import com.bookcloud.smartlibrary.repository.StaffAssignmentRepository;
import com.bookcloud.smartlibrary.repository.UserRepository;
import com.bookcloud.smartlibrary.service.StaffAssignmentService;

@Service
@Transactional
public class StaffAssignmentServiceImpl implements StaffAssignmentService {

	private final StaffAssignmentRepository repository;
	private final UserRepository userRepository;
	private final LibraryBranchRepository branchRepository;

	public StaffAssignmentServiceImpl(
			StaffAssignmentRepository repository,
			UserRepository userRepository,
			LibraryBranchRepository branchRepository) {
		this.repository = repository;
		this.userRepository = userRepository;
		this.branchRepository = branchRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<StaffAssignment> listAll() {
		return repository.findAllByOrderByCreatedAtDesc();
	}

	@Override
	@Transactional(readOnly = true)
	public List<StaffAssignment> listByBranch(Long branchId) {
		return repository.findByBranch_IdOrderByCreatedAtDesc(branchId);
	}

	@Override
	@Transactional(readOnly = true)
	public StaffAssignment getById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Staff assignment not found"));
	}

	@Override
	public StaffAssignment create(CreateStaffAssignmentRequest req, Role role) {
		requireAdmin(role);
		StaffAssignment assignment = new StaffAssignment();
		assignment.setUser(resolveUser(req.getUserUid()));
		assignment.setBranch(resolveBranch(req.getBranchId()));
		assignment.setStaffRole(req.getStaffRole());
		assignment.setActive(req.isActive());
		return repository.save(assignment);
	}

	@Override
	public StaffAssignment update(Long id, UpdateStaffAssignmentRequest req, Role role) {
		requireAdmin(role);
		StaffAssignment assignment = getById(id);
		if (req.getUserUid() != null) {
			assignment.setUser(resolveUser(req.getUserUid()));
		}
		if (req.getBranchId() != null) {
			assignment.setBranch(resolveBranch(req.getBranchId()));
		}
		if (req.getStaffRole() != null) {
			assignment.setStaffRole(req.getStaffRole());
		}
		if (req.getActive() != null) {
			assignment.setActive(req.getActive());
		}
		return repository.save(assignment);
	}

	@Override
	public void delete(Long id, Role role) {
		requireAdmin(role);
		repository.delete(getById(id));
	}

	private void requireAdmin(Role role) {
		if (role != Role.ADMIN) {
			throw new BusinessRuleException("Admin access required");
		}
	}

	private AppUser resolveUser(String uid) {
		return userRepository.findByUid(uid)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private LibraryBranch resolveBranch(Long id) {
		if (id == null) {
			return null;
		}
		return branchRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
	}
}

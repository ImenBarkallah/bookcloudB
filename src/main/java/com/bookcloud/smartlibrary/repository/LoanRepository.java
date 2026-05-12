package com.bookcloud.smartlibrary.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookcloud.smartlibrary.enums.LoanStatus;
import com.bookcloud.smartlibrary.model.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {

	@EntityGraph(attributePaths = { "book", "user", "branch", "copy" })
	List<Loan> findByUser_UidOrderByBorrowedAtDesc(String uid);

	long countByUser_UidAndStatusIn(String uid, Collection<LoanStatus> statuses);

	@EntityGraph(attributePaths = { "book", "user", "branch", "copy" })
	List<Loan> findByStatusAndDueAtBefore(LoanStatus status, Instant dueAt);

	@EntityGraph(attributePaths = { "book", "user", "branch", "copy" })
	@Query("select l from Loan l order by l.borrowedAt desc")
	List<Loan> findAllForAdmin();

	@Query(
			value = """
					select date_format(borrowed_at, '%Y-%m') as month_key, count(*) as loan_count
					from loans
					where borrowed_at is not null
					group by month_key
					order by month_key desc
					""",
			nativeQuery = true)
	List<Object[]> monthlyLoanStats();
}

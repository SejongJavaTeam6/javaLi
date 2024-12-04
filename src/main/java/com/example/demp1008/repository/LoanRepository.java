package com.example.demp1008.repository;

import com.example.demp1008.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    @Query("SELECT l FROM Loan l JOIN FETCH l.book WHERE l.member.id = :memberId")
    List<Loan> findByMemberId(@Param("memberId") Long memberId);
}
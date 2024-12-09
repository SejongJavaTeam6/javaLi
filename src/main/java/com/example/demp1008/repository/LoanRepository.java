package com.example.demp1008.repository;

import com.example.demp1008.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // 기존 모든 대출 기록 조회 (반납 포함)
    @Query("SELECT l FROM Loan l JOIN FETCH l.book WHERE l.member.id = :memberId")
    List<Loan> findByMemberId(@Param("memberId") Long memberId);

    // 새로 추가: 반납되지 않은 (returned = false) 대출 기록만 조회
    @Query("SELECT l FROM Loan l JOIN FETCH l.book WHERE l.member.id = :memberId AND l.returned = false")
    List<Loan> findByMemberIdAndReturnedFalse(@Param("memberId") Long memberId);
}
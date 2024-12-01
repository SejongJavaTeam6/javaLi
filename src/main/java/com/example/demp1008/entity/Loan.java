package com.example.demp1008.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 대출된 책
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // 대출한 회원
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 대출 날짜
    private LocalDate loanDate;

    // 반납 예정일 (반납되지 않았다면 null)
    private LocalDate returnDate;
    //연체일
    private int overdueDays;

}

package com.example.demp1008.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String bookNumber;
    private int loanCnt;
    private boolean isAvailable;
    private String title;
    private String publisher;
    private int pageCnt;

    // 대출 기록
    @OneToMany(mappedBy = "book")
    private List<Loan> loans;

}

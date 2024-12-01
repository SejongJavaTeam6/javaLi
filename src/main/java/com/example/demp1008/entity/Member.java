package com.example.demp1008.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private int age;
    private String  phoneNumber;
    private String email;
    private int borrowedCnt;

    // 대출 기록
    @OneToMany(mappedBy = "member")
    private List<Loan> loans;

}

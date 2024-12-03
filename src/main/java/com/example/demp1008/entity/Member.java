package com.example.demp1008.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String password;// 비밀번호 추가
    private int age;
    private String  phoneNumber;
    private String email;
    private int borrowedCnt;

    // 대출 기록
    @JsonIgnore // 순환 참조 방지 위해 추가
    @OneToMany(mappedBy = "member")
    private List<Loan> loans;

}

package com.example.demp1008.controller;

import com.example.demp1008.entity.Loan;
import com.example.demp1008.service.LoanService;
import com.example.demp1008.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("loan")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;
    private final MemberService memberService;
    //대출 검색 엔드포인트 추가
    @GetMapping("/loans")
    public List<Loan> getMemberLoans(@RequestParam String email) {
        return loanService.getMemberLoans(email);
    }

    @PostMapping("/borrow")
    public Loan borrowByMember(@RequestParam String email, @RequestParam String bookNumber){
        return loanService.borrowBook(email,bookNumber);


    }
}

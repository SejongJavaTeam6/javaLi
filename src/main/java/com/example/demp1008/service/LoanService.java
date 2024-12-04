package com.example.demp1008.service;

import com.example.demp1008.entity.Book;
import com.example.demp1008.entity.Loan;
import com.example.demp1008.entity.Member;
import com.example.demp1008.repository.BookRepository;
import com.example.demp1008.repository.LoanRepository;
import com.example.demp1008.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    /// 회원별 대출 가져오기
    @Transactional(readOnly = true)
    public List<Loan> getMemberLoans(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return loanRepository.findByMemberId(member.getId());
    }
    // 책 대출 메서드 추가
    @Transactional
    public Loan borrowBook(String email, String bookNumber)  {
        Book book = bookRepository.findByBookNumber(bookNumber)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available");
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 대출 처리
        book.setAvailable(false);
        book.setLoanCnt(book.getLoanCnt() + 1);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(LocalDate.now().plusWeeks(2)); // 2주 대출 기간
        //7권이상 대출 불가능하게
        if(member.getBorrowedCnt()+1>7){

            throw new RuntimeException("7권을 초과하셨습니다");
        }
        member.setBorrowedCnt(member.getBorrowedCnt() + 1);

        bookRepository.save(book);
        memberRepository.save(member);
        return loanRepository.save(loan);
    }

}

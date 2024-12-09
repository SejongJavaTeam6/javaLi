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

    /**
     * 회원별 대출 기록 가져오기
     * @param email 회원 이메일
     * @return 대출 목록
     */
    @Transactional(readOnly = true)
    public List<Loan> getMemberLoans(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        return loanRepository.findByMemberIdAndReturnedFalse(member.getId());
    }
    // 책 대출 메서드 추가
    @Transactional
    public Loan borrowBook(String email, String bookNumber) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        Book book = bookRepository.findByBookNumber(bookNumber)
                .orElseThrow(() -> new RuntimeException("도서가 존재하지 않습니다."));

        if (!book.isAvailable()) {
            throw new RuntimeException("해당 도서는 현재 대출 불가능 상태입니다.");
        }

        // 대출 기록 생성
        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setScheduledReturnDate(LocalDate.now().plusWeeks(2)); // 예: 대출 후 2주 후 반납 예정
        loan.setReturned(false);

        // 도서 상태 업데이트
        book.setAvailable(false);
        book.setLoanCnt(book.getLoanCnt() + 1);

        loanRepository.save(loan);
        bookRepository.save(book);

        return loan;
    }



    /**
     * 도서 반납 메서드
     * @param loanId 반납할 대출 기록의 ID
     */
    @Transactional
    public void returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("대출 기록이 존재하지 않습니다."));

        if (loan.isReturned()) {
            throw new RuntimeException("이미 반납된 도서입니다.");
        }

        // 반납 처리
        loan.setReturned(true);
        loan.setActualReturnDate(LocalDate.now());

        // 도서 상태 업데이트
        Book book = loan.getBook();
        book.setAvailable(true);

        loanRepository.save(loan);
        bookRepository.save(book);
    }



}

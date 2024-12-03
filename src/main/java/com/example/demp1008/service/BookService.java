package com.example.demp1008.service;

import com.example.demp1008.entity.Book;
import com.example.demp1008.entity.Loan;
import com.example.demp1008.entity.Member;
import com.example.demp1008.repository.BookRepository;
import com.example.demp1008.repository.LoanRepository;
import com.example.demp1008.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;  // 추가
    private final LoanRepository loanRepository;      // 추가

    //책 생성
    public Book createBook(Book book){
        if(!bookRepository.existsByBookNumber(book.getBookNumber())){
            bookRepository.save(book);
        }
        return book;


    }

    public List<Book> findAllBooks(){
        return bookRepository.findAll();

    }

    // 책 검색 추가
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return bookRepository.findByTitleContaining(keyword.trim());
    }

    //제목으로 찾는 거 추가
    public Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    // 책 대출 메서드 추가
    public Loan borrowBook(Long memberId, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 대출 처리
        book.setAvailable(false);
        book.setLoanCnt(book.getLoanCnt() + 1);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(LocalDate.now().plusWeeks(2)); // 2주 대출 기간

        member.setBorrowedCnt(member.getBorrowedCnt() + 1);

        bookRepository.save(book);
        memberRepository.save(member);
        return loanRepository.save(loan);
    }

}

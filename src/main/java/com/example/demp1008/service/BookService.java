package com.example.demp1008.service;

import com.example.demp1008.entity.Book;
import com.example.demp1008.repository.BookRepository;
import com.example.demp1008.repository.LoanRepository;
import com.example.demp1008.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;  // 추가
    private final LoanRepository loanRepository;      // 추가

    //책 생성
    public Book createBook(Book book){
        if(!bookRepository.existsByBookNumber(book.getBookNumber())){
            book.setAvailable(true);
            book.setLoanCnt(0);
            bookRepository.save(book);
        }
        return book;


    }

    public List<Book> findAllBooks(){
        return bookRepository.findAll();

    }

//    // 책 검색 추가
//    public List<Book> searchBooks(String keyword) {
//        if (keyword == null || keyword.trim().isEmpty()) {
//            return Collections.emptyList();
//        }
//        return bookRepository.findByTitleContaining(keyword.trim());
//    }

    //제목으로 찾는 거 추가
    public Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }



}

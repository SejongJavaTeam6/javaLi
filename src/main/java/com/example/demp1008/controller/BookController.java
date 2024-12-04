package com.example.demp1008.controller;

import com.example.demp1008.entity.Book;
import com.example.demp1008.entity.Loan;
import com.example.demp1008.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping("/create")
    public Book createBook(@ModelAttribute Book book){
        return bookService.createBook(book);
    }

    @GetMapping("/read")
    public List<Book> findAllBooks(){

        return bookService.findAllBooks();
    }

    //로그인 후 책 검색용으로 추가
    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam String keyword) {
        return bookService.searchBooks(keyword);
    }

    // 도서 대출 추가
    @PostMapping("/borrow")
    public Loan borrowBook(@RequestParam Long memberId, @RequestParam Long bookId) {
        return bookService.borrowBook(memberId, bookId);
    }

}

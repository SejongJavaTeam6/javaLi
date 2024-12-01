package com.example.demp1008.controller;

import com.example.demp1008.entity.Book;
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

}

package com.example.demp1008.service;

import com.example.demp1008.entity.Book;
import com.example.demp1008.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

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

}

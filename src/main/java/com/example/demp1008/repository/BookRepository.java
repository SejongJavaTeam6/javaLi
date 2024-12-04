package com.example.demp1008.repository;

import com.example.demp1008.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    boolean existsByBookNumber(String bookNumber);
    List<Book> findByTitleContaining(String title); // 제목 검색 추가
    List<Book> findByPublisherContaining(String publisher); // 지은이 검색 추가
    Optional<Book> findByTitle(String title);
    Optional<Book> findByBookNumber(String bookNumber);
}

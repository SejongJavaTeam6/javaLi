package com.example.demp1008.repository;

import com.example.demp1008.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    boolean existsByBookNumber(String bookNumber);
}

package com.elsherbieny.bookStore.repository;

import com.elsherbieny.bookStore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}

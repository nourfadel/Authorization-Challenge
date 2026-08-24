package com.elsherbieny.bookStore.service;

import com.elsherbieny.bookStore.dto.BookRequest;
import com.elsherbieny.bookStore.dto.BookResponse;
import com.elsherbieny.bookStore.exception.NotFoundException;
import com.elsherbieny.bookStore.model.Book;
import com.elsherbieny.bookStore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookResponse create(BookRequest request) {
        Book book = new Book(request.title(), request.author(), request.publishedDate());
        return BookResponse.from(bookRepository.save(book));
    }

    public List<BookResponse> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    public BookResponse findById(Long id) {
        return BookResponse.from(findBook(id));
    }

    public BookResponse update(Long id, BookRequest request) {
        Book book = findBook(id);
        book.update(request.title(), request.author(), request.publishedDate());
        return BookResponse.from(bookRepository.save(book));
    }

    public void delete(Long id) {
        Book book = findBook(id);
        bookRepository.delete(book);
    }

    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with id " + id + " was not found"));
    }
}

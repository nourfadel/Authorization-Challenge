package com.elsherbieny.bookStore.dto;

import com.elsherbieny.bookStore.model.Book;

import java.time.LocalDate;

public record BookResponse(
        Long id,
        String title,
        String author,
        LocalDate publishedDate
)
{
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublishedDate()
        );
    }
}

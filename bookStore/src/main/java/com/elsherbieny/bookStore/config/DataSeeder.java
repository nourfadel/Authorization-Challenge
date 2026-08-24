package com.elsherbieny.bookStore.config;

import com.elsherbieny.bookStore.model.User;
import com.elsherbieny.bookStore.model.Book;
import com.elsherbieny.bookStore.model.Role;
import com.elsherbieny.bookStore.repository.UserRepository;
import com.elsherbieny.bookStore.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository appUserRepository;
    private final BookRepository bookRepository;

    public DataSeeder(UserRepository appUserRepository, BookRepository bookRepository) {
        this.appUserRepository = appUserRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) {
        if (appUserRepository.count() == 0) {
            appUserRepository.save(new User("admin", Role.ADMIN));
            appUserRepository.save(new User("editor", Role.EDITOR));
            appUserRepository.save(new User("viewer", Role.VIEWER));
        }

        if (bookRepository.count() == 0) {
            bookRepository.save(new Book("Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1)));
        }
    }
}

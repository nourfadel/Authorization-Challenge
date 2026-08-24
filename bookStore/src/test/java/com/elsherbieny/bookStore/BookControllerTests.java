package com.elsherbieny.bookStore;

import com.elsherbieny.bookStore.model.User;
import com.elsherbieny.bookStore.model.Book;
import com.elsherbieny.bookStore.model.Role;
import com.elsherbieny.bookStore.repository.UserRepository;
import com.elsherbieny.bookStore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository appUserRepository;

    @Autowired
    private BookRepository bookRepository;

    private User admin;
    private User editor;
    private User viewer;
    private Book book;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        appUserRepository.deleteAll();

        admin = appUserRepository.save(new User("admin-test", Role.ADMIN));
        editor = appUserRepository.save(new User("editor-test", Role.EDITOR));
        viewer = appUserRepository.save(new User("viewer-test", Role.VIEWER));
        book = bookRepository.save(new Book("Existing Book", "Author", LocalDate.of(2020, 1, 1)));
    }

    @Test
    void adminCanCreateReadUpdateAndDeleteBooks() throws Exception {
        mockMvc.perform(post("/books")
                        .header("X-User-Id", admin.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "1984",
                                  "author": "George Orwell",
                                  "publishedDate": "1949-06-08"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("1984"));

        mockMvc.perform(get("/books").header("X-User-Id", admin.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(put("/books/{id}", book.getId())
                        .header("X-User-Id", admin.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "1984",
                                  "author": "George Orwell",
                                  "publishedDate": "1949-06-08"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1984"));

        mockMvc.perform(delete("/books/{id}", book.getId()).header("X-User-Id", admin.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void editorCanCreateReadAndUpdateButCannotDeleteBooks() throws Exception {
        mockMvc.perform(post("/books")
                        .header("X-User-Id", editor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "To Kill a Mockingbird",
                                  "author": "Harper Lee",
                                  "publishedDate": "1960-07-11"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/books").header("X-User-Id", editor.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(put("/books/{id}", book.getId())
                        .header("X-User-Id", editor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "To Kill a Mockingbird",
                                  "author": "Harper Lee",
                                  "publishedDate": "1960-07-11"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/books/{id}", book.getId()).header("X-User-Id", editor.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerCanReadButCannotCreateUpdateOrDeleteBooks() throws Exception {
        mockMvc.perform(post("/books")
                        .header("X-User-Id", viewer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Brave New World",
                                  "author": "Aldous Huxley",
                                  "publishedDate": "1932-08-01"
                                }
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/books").header("X-User-Id", viewer.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/books/{id}", book.getId()).header("X-User-Id", viewer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()));

        mockMvc.perform(put("/books/{id}", book.getId())
                        .header("X-User-Id", viewer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Brave New World",
                                  "author": "Aldous Huxley",
                                  "publishedDate": "1932-08-01"
                                }
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/books/{id}", book.getId()).header("X-User-Id", viewer.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidBookRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/books")
                        .header("X-User-Id", admin.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "author": "",
                                  "publishedDate": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasSize(3)));
    }

    @Test
    void missingUserHeaderReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void invalidUserHeaderReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/books").header("X-User-Id", "wrong"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}

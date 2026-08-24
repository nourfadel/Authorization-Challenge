package com.elsherbieny.bookStore.repository;

import com.elsherbieny.bookStore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

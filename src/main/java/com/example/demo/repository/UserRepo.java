package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepo extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);

    @Query("""
            select u from User u
            where lower(u.firstName) like lower(concat('%', :kw, '%'))
               or lower(u.lastName) like lower(concat('%', :kw, '%'))
               or lower(concat(u.firstName, ' ', u.lastName)) like lower(concat('%', :kw, '%'))
               or lower(u.email) like lower(concat('%', :kw, '%'))
            """)
    Page<User> search(@Param("kw") String keyword, Pageable pageable);
}


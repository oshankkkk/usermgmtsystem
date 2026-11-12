package com.example.demo.service;

import com.example.demo.dto.Request;
import com.example.demo.dto.Response;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import java.util.Locale;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private static final Set<String> SORTABLE_FIELDS =
            Set.of("id", "firstName", "lastName", "email", "username", "role", "active", "createdAt");

    private final UserRepo repo;

    public UserService(UserRepo userRepository) {
        this.repo = userRepository;
    }

    /** Builds a safe Pageable: unknown sort fields fall back to "id", size is clamped to 1..100. */
    public Pageable buildPageable(int page, int size, String sortBy, String direction) {
        String field = SORTABLE_FIELDS.contains(sortBy) ? sortBy : "id";
        Sort sort = "desc".equalsIgnoreCase(direction) ? Sort.by(field).descending() : Sort.by(field).ascending();
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), sort);
    }

    @Transactional(readOnly = true)
    public Page<Response> findAll(Pageable pageable) {
        return repo.findAll(pageable).map(Response::from);
    }

    @Transactional(readOnly = true)
    public Page<Response> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return findAll(pageable);
        }
        return repo.search(keyword.trim(), pageable).map(Response::from);
    }

    @Transactional(readOnly = true)
    public Response getById(Long id) {
        return Response.from(findEntity(id));
    }

    /** Pre-fills the edit form. */
    @Transactional(readOnly = true)
    public Request getRequestById(Long id) {
        User u = findEntity(id);
        Request r = new Request();
        r.setFirstName(u.getFirstName());
        r.setLastName(u.getLastName());
        r.setEmail(u.getEmail());
        r.setUsername(u.getUsername());
        r.setPhone(u.getPhone());
        r.setRole(u.getRole());
        r.setActive(u.isActive());
        return r;
    }

    @Transactional
    public Response create(Request request) {
        String email = normalizeEmail(request.getEmail());
        String username = request.getUsername().trim();

        if (repo.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("email", "Email is already in use");
        }
        if (repo.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException("username", "Username is already taken");
        }

        User user = new User();
        apply(user, request);
        return Response.from(repo.saveAndFlush(user));
    }

    @Transactional
    public Response update(Long id, Request request) {
        User user = findEntity(id);
        String email = normalizeEmail(request.getEmail());
        String username = request.getUsername().trim();

        if (repo.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateResourceException("email", "Email is already in use");
        }
        if (repo.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
            throw new DuplicateResourceException("username", "Username is already taken");
        }

        apply(user, request);
        return Response.from(repo.saveAndFlush(user));
    }

    /** soft = true only sets active = false; soft = false removes the row. */
    @Transactional
    public void delete(Long id, boolean soft) {
        User user = findEntity(id);
        if (soft) {
            user.setActive(false);
            repo.save(user);
        } else {
            repo.delete(user);
        }
    }

    private User findEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    private void apply(User user, Request r) {
        user.setFirstName(r.getFirstName().trim());
        user.setLastName(r.getLastName().trim());
        user.setEmail(normalizeEmail(r.getEmail()));
        user.setUsername(r.getUsername().trim());
        user.setPhone(r.getPhone() == null || r.getPhone().isBlank() ? null : r.getPhone().trim());
        user.setRole(r.getRole());
        user.setActive(r.isActive());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

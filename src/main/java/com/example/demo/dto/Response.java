package com.example.demo.dto;

import com.example.demo.model.User;
import java.time.LocalDateTime;
import com.example.demo.model.Role;

public class Response {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phone;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Response from(User u) {
        Response r = new Response();
        r.id = u.getId();
        r.firstName = u.getFirstName();
        r.lastName = u.getLastName();
        r.email = u.getEmail();
        r.username = u.getUsername();
        r.phone = u.getPhone();
        r.role = u.getRole();
        r.active = u.isActive();
        r.createdAt = u.getCreatedAt();
        r.updatedAt = u.getUpdatedAt();
        return r;
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

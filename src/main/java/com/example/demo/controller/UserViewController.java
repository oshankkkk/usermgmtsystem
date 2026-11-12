package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.dto.Request;
import com.example.demo.dto.Response;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.repository.UserStatsJdbcRepository;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Thymeleaf pages: list, create form, edit form, details, delete confirmation. */
@Controller
public class UserViewController {

    private final UserService userService;
    private final UserStatsJdbcRepository statsRepository;

    public UserViewController(UserService userService, UserStatsJdbcRepository statsRepository) {
        this.userService = userService;
        this.statsRepository = statsRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/users";
    }

    @GetMapping("/users")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "asc") String dir,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        Page<Response> result = userService.search(keyword, userService.buildPageable(page, size, sortBy, dir));
        model.addAttribute("page", result);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("dir", dir);
        model.addAttribute("stats", statsRepository.getStats());
        return "users/list";
    }

    @GetMapping("/users/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getById(id));
        return "users/detail";
    }

    // ---------- create ----------

    @GetMapping("/users/new")
    public String newForm(Model model) {
        model.addAttribute("user", new Request());
        prepareForm(model, "Create user", "/users");
        return "users/form";
    }

    @PostMapping("/users")
    public String create(@Valid @ModelAttribute("user") Request request,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                Response created = userService.create(request);
                redirect.addFlashAttribute("success", "User '" + created.getUsername() + "' was created.");
                return "redirect:/users";
            } catch (DuplicateResourceException e) {
                result.rejectValue(e.getField(), "duplicate", e.getMessage());
            }
        }
        prepareForm(model, "Create user", "/users");
        return "users/form";
    }

    // ---------- edit ----------

    @GetMapping("/users/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getRequestById(id));
        prepareForm(model, "Edit user", "/users/" + id);
        return "users/form";
    }

    @PostMapping("/users/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("user") Request request,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                Response updated = userService.update(id, request);
                redirect.addFlashAttribute("success", "User '" + updated.getUsername() + "' was updated.");
                return "redirect:/users";
            } catch (DuplicateResourceException e) {
                result.rejectValue(e.getField(), "duplicate", e.getMessage());
            }
        }
        prepareForm(model, "Edit user", "/users/" + id);
        return "users/form";
    }

    // ---------- delete (with confirmation page) ----------

    @GetMapping("/users/{id}/delete")
    public String confirmDelete(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getById(id));
        return "users/delete";
    }

    @PostMapping("/users/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(defaultValue = "false") boolean soft,
                         RedirectAttributes redirect) {
        userService.delete(id, soft);
        redirect.addFlashAttribute("success", soft ? "User was deactivated." : "User was deleted.");
        return "redirect:/users";
    }

    private void prepareForm(Model model, String title, String formAction) {
        model.addAttribute("roles", Role.values());
        model.addAttribute("title", title);
        model.addAttribute("formAction", formAction);
    }
}

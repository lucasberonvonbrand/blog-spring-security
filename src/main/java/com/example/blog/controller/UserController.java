package com.example.blog.controller;

import com.example.blog.model.Author;
import com.example.blog.model.Role;
import com.example.blog.model.UserSec;
import com.example.blog.service.IAuthorService;
import com.example.blog.service.IRoleService;
import com.example.blog.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IAuthorService authorService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserSec>> getAllUsers() {
        List<UserSec> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserSec> getUserById(@PathVariable Long id) {
        Optional<UserSec> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserSec> createUser(@RequestBody UserSec userSec) {

        Set<Role> roleList = new HashSet<>();
        for (Role role : userSec.getRolesList()) {
            roleService.findById(role.getId()).ifPresent(roleList::add);
        }

        userSec.setPassword(userService.encriptPassword(userSec.getPassword()));

        if (roleList.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        userSec.setRolesList(roleList);

        boolean isAuthorRole = roleList.stream()
                .anyMatch(role -> role.getRole().equalsIgnoreCase("AUTHOR"));

        if (isAuthorRole && userSec.getAuthor() != null) {
            Author author = userSec.getAuthor();
            author.setUser(userSec);
        }

        UserSec savedUser = userService.save(userSec);

        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserSec> updateUser(@PathVariable Long id, @RequestBody UserSec userUpdate) {
        return userService.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(userUpdate.getUsername());
                    if (userUpdate.getPassword() != null) {
                        existingUser.setPassword(userService.encriptPassword(userUpdate.getPassword()));
                    }

                    if (userUpdate.getRolesList() != null) {
                        Set<Role> roleList = new HashSet<>();
                        for (Role role : userUpdate.getRolesList()) {
                            roleService.findById(role.getId()).ifPresent(roleList::add);
                        }
                        existingUser.setRolesList(roleList);
                    }

                    if (existingUser.getRolesList().stream().anyMatch(r -> r.getRole().equalsIgnoreCase("AUTHOR"))
                            && userUpdate.getAuthor() != null) {
                        Author author = userUpdate.getAuthor();
                        author.setUser(existingUser);
                        existingUser.setAuthor(author);
                    }
                    return ResponseEntity.ok(userService.save(existingUser));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}


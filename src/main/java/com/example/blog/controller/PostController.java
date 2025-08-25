package com.example.blog.controller;

import com.example.blog.model.Post;
import com.example.blog.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private IPostService postService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER','AUTHOR')")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER','AUTHOR')")
    public Post getPost(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @PostMapping("/{authorId}")
    @PreAuthorize("hasAnyRole('ADMIN','AUTHOR')")
    public Post createPost(@PathVariable Long authorId, @RequestBody Post post) {
        return postService.createPost(authorId, post);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AUTHOR')")
    public Post updatePost(@PathVariable Long id, @RequestBody Post post) {
        return postService.updatePost(id, post);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }
}


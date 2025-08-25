package com.example.blog.service;

import com.example.blog.model.Post;

import java.util.List;

public interface IPostService {

    Post createPost(Long authorId, Post post);

    List<Post> getAllPosts();

    Post getPostById(Long id);

    Post updatePost(Long id, Post postDetails);

    void deletePost(Long id);
}


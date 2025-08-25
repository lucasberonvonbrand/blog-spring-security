package com.example.blog.service;

import com.example.blog.model.Author;

import java.util.List;

public interface IAuthorService {

    List<Author> getAllAuthors();

    Author getAuthorById(Long id);

    Author updateAuthor(Long id, Author author);

    void deleteAuthor(Long id);
}

package com.example.demo.model.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    List<Author> findByNameStartsWithIgnoreCase(String prefix);

    @Query("select a from Author a left join fetch a.books")
    List<Author> findAllFetchBooks();

    Long countByNumberOfBooksGreaterThanAndNameContainsIgnoreCase(int count, String name);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Author a set a.numberOfBooks = a.numberOfBooks + 1 where a.id = :id")
    int incrementAuthorBooks(@Param("id") Integer id);
}

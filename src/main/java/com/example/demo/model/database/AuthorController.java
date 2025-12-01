package com.example.demo.model.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthorController {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorController(AuthorRepository repository, BookRepository bookRepository) {
        this.authorRepository = repository;
        this.bookRepository = bookRepository;
    }

    @GetMapping("/author")
    public List<Author> listAll(
            @RequestParam(required = false) String name
    ) {
        if (name == null || name.isBlank()) {
            return authorRepository.findAll();
        }

        return authorRepository.findByNameStartsWithIgnoreCase(name);
    }

//    public void testMethod() {
//        List<Author> authors = authorRepository.findAllFetchBooks();
//        for (Author author: authors) {
//            System.out.println(author.getName() + " " + author.getBooks().size());
//        }
//    }



    @PersistenceContext
    private EntityManager manager;

    public List<Author> findAuthorsWithAtLeastBooksNum(int booksNum) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<Author> cq = cb.createQuery(Author.class);

        cq.select(
                cq.from(Author.class)
        ).where(
                cb.ge(cq.from(Author.class).get("numberOfBooks"), booksNum)
        );

        return manager.createQuery(cq).getResultList();
    }


    @GetMapping("/author/{id}")
    public Author findById(@PathVariable("id") Integer id) {
        return authorRepository
                .findById(id).orElseThrow(
                        () -> new IllegalArgumentException("Author with id " + id + " not found")
                );
    }


    public record AuthorDto(String name, String bio, int numberOfBooks) {
    }

    @PostMapping("/authors")
    public Author createAuthor(@RequestBody AuthorDto dto) {
        Author author = new Author(dto.name, dto.bio, dto.numberOfBooks);
        return authorRepository.save(author);
    }


    public record BookDto(String title) {}

    @Transactional
    @PostMapping("/author/{id}/books")
    public Book createBookWithAuthor(@PathVariable("id") int id, @RequestBody BookDto bookDto) {
        Author author = authorRepository.findById(id).orElseThrow(
                 () -> new IllegalArgumentException("Author with id " + id + " not found"));
        Book book = new Book(bookDto.title);
        book.setAuthor(author);

        book = bookRepository.save(book);
        // DB
        return book;
    }
}

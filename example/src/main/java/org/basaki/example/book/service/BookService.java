package org.basaki.example.book.service;

import org.basaki.example.book.data.entity.BookEntity;
import org.basaki.example.book.data.repository.BookRepository;
import org.basaki.example.book.error.DataNotFoundException;
import org.basaki.example.book.model.Author;
import org.basaki.example.book.model.Book;
import org.basaki.example.book.model.BookRequest;
import org.basaki.example.book.model.Genre;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;

/**
 * {@code BookService} service provides data access service for {@code Book}.
 * <p/>
 *
 * @author Indra Basak
 * @since 4/16/17
 */
@Service
@Slf4j
public class BookService {

    private static final String BOOK_WITH_ID = "Book with id ";

    private static final String NOT_FOUND = " not found!";

    private final BookRepository repo;

    private final Mapper mapper;

    @Autowired
    public BookService(BookRepository repo, Mapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional
    public Book create(BookRequest request) {
        validate(request);

        BookEntity entity = mapper.map(request, BookEntity.class);
        entity.setId(UUID.randomUUID());
        entity.setAuthorFirstName(request.getAuthor().getFirstName());
        entity.setAuthorLastName(request.getAuthor().getLastName());

        entity = repo.save(entity);

        Book book = mapEntityToModel(entity);

        log.info("Created book with id " + book.getId());

        return book;
    }

    public Book read(UUID id) {
        Optional<BookEntity> optional = repo.findById(id);

        if (!optional.isPresent()) {
            throw new DataNotFoundException(
                    BOOK_WITH_ID + id + NOT_FOUND);
        }

        BookEntity entity = optional.get();

        Book book = mapper.map(entity, Book.class);
        Author author = new Author();
        author.setFirstName(entity.getAuthorFirstName());
        author.setLastName(entity.getAuthorLastName());
        book.setAuthor(author);

        return book;
    }

    public List<Book> readAll(String title, Genre genre, String publisher,
            String authorFirstName, String authorLastName) {

        if (title == null && genre == null && publisher == null &&
                authorFirstName == null && authorLastName == null) {
            return map(repo.findAll());
        }

        BookEntity entity = new BookEntity();
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths(
                "star");

        if (title != null) {
            entity.setTitle(title);
            matcher = matcher.withMatcher("title", startsWith().ignoreCase());
        }

        if (genre != null) {
            entity.setGenre(genre);
        }

        if (publisher != null) {
            entity.setPublisher(publisher);
            matcher =
                    matcher.withMatcher("publisher", startsWith().ignoreCase());
        }

        if (authorFirstName != null) {
            entity.setAuthorFirstName(authorFirstName);
            matcher =
                    matcher.withMatcher("authorFirstName",
                            startsWith().ignoreCase());
        }

        if (authorLastName != null) {
            entity.setAuthorLastName(authorLastName);
            matcher =
                    matcher.withMatcher("authorLastName",
                            contains().ignoreCase());
        }

        Example<BookEntity> example = Example.of(entity, matcher);

        return map(repo.findAll(example));
    }

    @Transactional
    public Book update(UUID id, BookRequest request) {
        Optional<BookEntity> optional = repo.findById(id);
        if (!optional.isPresent()) {
            throw new DataNotFoundException(
                    BOOK_WITH_ID + id + NOT_FOUND);
        }

        BookEntity entity = optional.get();
        validate(request);

        mapper.map(request, BookEntity.class);
        mapper.map(request, request);
        entity.setAuthorFirstName(request.getAuthor().getFirstName());
        entity.setAuthorLastName(request.getAuthor().getLastName());

        entity = repo.save(entity);

        Book book = mapEntityToModel(entity);

        log.info("Updated book with id " + book.getId());

        return book;
    }

    @Transactional
    public void delete(UUID id) {
        try {
            repo.deleteById(id);
        } catch (Exception e) {
            throw new DataNotFoundException(
                    BOOK_WITH_ID + id + NOT_FOUND);
        }
    }

    @Transactional
    public void deleteAll() {
        repo.deleteAll();
    }

    public List<String> getPublisher(String publisher) {
        return repo.findDistinctPublisher(publisher);
    }

    private void validate(BookRequest request) {
        Assert.notNull(request.getTitle(), "Title should not be null.");
        Assert.notNull(request.getGenre(), "Genre should not be null.");
        Assert.notNull(request.getPublisher(), "Publisher should not be null.");
        Assert.notNull(request.getAuthor(), "Author should not be null.");
        Assert.state((request.getStar() > 0 && request.getStar() <= 5),
                "Star should be between 1 and 5");
    }

    private Book mapEntityToModel(BookEntity entity) {
        Book book = mapper.map(entity, Book.class);
        Author author = new Author();
        author.setFirstName(entity.getAuthorFirstName());
        author.setLastName(entity.getAuthorLastName());
        book.setAuthor(author);

        return book;
    }

    private List<Book> map(List<BookEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            throw new DataNotFoundException(
                    "No books found with the search criteria!");
        }

        return entities.stream().map(this::mapEntityToModel).collect(
                Collectors.toList());
    }
}

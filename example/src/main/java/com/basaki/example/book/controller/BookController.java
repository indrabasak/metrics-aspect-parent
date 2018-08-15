package com.basaki.example.book.controller;

import com.basaki.example.book.model.Book;
import com.basaki.example.book.model.BookRequest;
import com.basaki.example.book.model.Genre;
import com.basaki.example.book.service.BookService;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code BookController} is the spring REST controller for book API. Exposes
 * all CRUD operations on book.
 * <p/>
 *
 * @author Indra Basak
 * @since 4/16/17
 */
@SuppressWarnings("MVCPathVariableInspection")
@RestController
@Slf4j
@Api(value = "Book API",
        produces = "application/json", tags = {"API"})
@Timed
@Metered
@ExceptionMetered
public class BookController {

    private static final String BOOK_URL = "/books";

    private static final String BOOK_BY_ID_URL = BOOK_URL + "/{id}";

    private static final String PUBLISHER_URL = BOOK_URL + "/publishers";

    private BookService service;

    @Autowired
    public BookController(BookService service) {
        this.service = service;
    }

    @ApiOperation(
            value = "Creates a book.",
            notes = "Requires a book title, genre, publisher, star, and author.",
            response = Book.class)
    @ApiResponses({
            @ApiResponse(code = 201, response = Book.class,
                    message = "Book created successfully")})
    @PostMapping(value = BOOK_URL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody BookRequest request) {
        return service.create(request);
    }

    @ApiOperation(
            value = "Retrieves a book by ID.",
            notes = "Requires a book identifier",
            response = Book.class)
    @GetMapping(value = BOOK_BY_ID_URL,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Book read(@ApiParam(value = "Book ID", required = true)
    @PathVariable("id") UUID id) {
        return service.read(id);
    }

    @ApiOperation(
            value = "Retrieves all the books associated with the search string.",
            notes = "In absence of any parameter, it will return all the books.",
            response = Book.class, responseContainer = "List")
    @GetMapping(value = BOOK_URL,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public List<Book> readAll(
            @ApiParam(value = "Search string to search a book based on search criteria. Returns all books if empty.")
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "genre", required = false) Genre genre,
            @RequestParam(value = "publisher", required = false) String publisher,
            @RequestParam(value = "firstName", required = false) String authorFirstName,
            @RequestParam(value = "lastName", required = false) String authorLastName) {
        return service.readAll(title, genre, publisher, authorFirstName,
                authorLastName);
    }

    @ApiOperation(value = "Updates a book.", response = Book.class)
    @ApiResponses({
            @ApiResponse(code = 201, response = Book.class,
                    message = "Updated a book created successfully")})
    @PutMapping(value = BOOK_BY_ID_URL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Book update(@ApiParam(value = "Book ID", required = true)
    @PathVariable("id") UUID id,
            @RequestBody BookRequest request) {
        return service.update(id, request);
    }

    @ApiOperation(value = "Deletes a book by ID.")
    @DeleteMapping(value = BOOK_BY_ID_URL)
    @ResponseBody
    public void delete(@ApiParam(value = "Book ID", required = true)
    @PathVariable("id") UUID id) {
        service.delete(id);
    }

    @ApiOperation(value = "Deletes all books.")
    @DeleteMapping(value = BOOK_URL)
    @ResponseBody
    public void deleteAll() {
        service.deleteAll();
    }

    @ApiOperation(
            value = "Retrieves a list of distinct publisher based on partial publisher name search.",
            notes = "Requires a partial publisher name",
            response = String.class, responseContainer = "List")
    @GetMapping(value = PUBLISHER_URL,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public List<String> getPublisher(
            @ApiParam(value = "partial publisher name", required = true)
            @RequestParam(value = "q") String publisher) {
        return service.getPublisher(publisher);
    }
}

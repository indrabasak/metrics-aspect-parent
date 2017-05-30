package com.basaki.example.book.data.repository;

import com.basaki.example.book.data.entity.BookEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * {@code BookRepository} exposes all CRUD operations on a data of type
 * {@code Book}.
 * <p/>
 *
 * @author Indra Basak
 * @since 4/16/17
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, UUID> {

    @Query("SELECT DISTINCT b.publisher FROM BookEntity b WHERE " +
            "UPPER(b.publisher) LIKE UPPER(CONCAT('%', ?1, '%'))")
    List<String> findDistinctPublisher(String publisher);
}

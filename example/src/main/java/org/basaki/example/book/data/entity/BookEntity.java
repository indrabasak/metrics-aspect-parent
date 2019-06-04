package org.basaki.example.book.data.entity;

import org.basaki.example.book.model.Genre;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@code BookEntity} represents a row in the <code>Books</code> database table.
 * <p/>
 *
 * @author Indra Basak
 * @since 4/16/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class BookEntity implements Serializable {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "genre", nullable = false)
    private Genre genre;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Column(name = "star", nullable = false)
    private int star;

    @Column(name = "author_first_name", nullable = false)
    private String authorFirstName;

    @Column(name = "author_last_name", nullable = false)
    private String authorLastName;
}

package org.sawyron.librarybookservice.authors;

import jakarta.persistence.*;
import org.sawyron.librarybookservice.books.Book;

import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "authors",
        indexes = @Index(columnList = "name")
)
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "name",
            nullable = false,
            length = 100,
            unique = true
    )
    private String name;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Book> books;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}

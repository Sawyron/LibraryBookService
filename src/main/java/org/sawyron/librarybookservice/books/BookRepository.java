package org.sawyron.librarybookservice.books;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    @Query("select b from Book b join fetch b.author where b.id = :id")
    Optional<Book> findByIdWithAuthor(UUID id);

    boolean existsByTitleAndAuthorIdAndPublishedDate(String title, UUID authorId, LocalDate publicationDate);
}

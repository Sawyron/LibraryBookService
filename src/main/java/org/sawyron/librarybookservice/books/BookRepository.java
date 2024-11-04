package org.sawyron.librarybookservice.books;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    @Query("select b from Book b join fetch b.author where b.id = :id")
    Optional<Book> findByIdWithAuthor(UUID id);

    @Query("select b from Book b join fetch b.author order by b.publishedDate desc")
    Page<Book> findAllBooksWithAuthors(Pageable pageable);

    boolean existsByTitleAndAuthorIdAndPublishedDate(String title, UUID authorId, LocalDate publicationDate);
}

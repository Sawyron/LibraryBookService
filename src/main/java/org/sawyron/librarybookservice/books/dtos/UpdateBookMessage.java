package org.sawyron.librarybookservice.books.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateBookMessage(UUID id, String title, String author, LocalDate publishedDate) {
}

package org.sawyron.librarybookservice.books.dtos;

import java.time.LocalDate;

public record CreateBookMessage(String title, String author, LocalDate publishedDate) {
}

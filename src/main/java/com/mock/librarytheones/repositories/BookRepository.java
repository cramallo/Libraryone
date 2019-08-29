package com.mock.librarytheones.repositories;
import com.mock.librarytheones.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
    Book findOneByIsbn(String isbn);
}

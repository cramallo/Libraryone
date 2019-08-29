package com.mock.librarytheones.services;
import com.mock.librarytheones.models.Book;
import com.mock.librarytheones.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    @Autowired
    BookRepository bookRepository;

    public Book createBook(Book book){
        book.setPrice(generateRandomPrice());
        return bookRepository.save(book);
    }

    public Book getBookByIsbn(String isbn){
        return bookRepository.findOneByIsbn(isbn);
    }

    private float generateRandomPrice(){
        double random =  (500 + Math.random() * (20000 - 500));
        return Math.round(random);
    }

}

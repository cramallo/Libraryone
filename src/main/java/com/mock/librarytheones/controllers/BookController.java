package com.mock.librarytheones.controllers;
import com.mock.librarytheones.constants.SwaggerMessages;
import com.mock.librarytheones.dtos.BookApiDTO;
import com.mock.librarytheones.models.Book;
import com.mock.librarytheones.services.BookService;
import com.mock.librarytheones.services.OpenLibraryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@Api(description="Book REST API", produces = "application/json", tags = {"Book"})
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private OpenLibraryService openLibraryService;

    @GetMapping("/isbn/{isbn}")
    @ApiOperation(value="Giving an isbn, return one book", response = BookApiDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = SwaggerMessages.findSuccess),
            @ApiResponse(code = 400, message = SwaggerMessages.badRequest),
            @ApiResponse(code = 404, message = SwaggerMessages.notFound),
            @ApiResponse(code = 500, message = SwaggerMessages.internalServerError)
    })
    public ResponseEntity<BookApiDTO> getExternalBook(@PathVariable("isbn") String isbn) throws IOException {
        Book persistedBook = bookService.getBookByIsbn(isbn);
        if(persistedBook!=null){
            BookApiDTO bookResponse = convertEntityToBookApiDto(persistedBook);
            return new ResponseEntity<BookApiDTO>(bookResponse, HttpStatus.OK);
        }
        BookApiDTO bookApiDto =  openLibraryService.bookInfo(isbn);
        Book book = convertBookApiDtoToEntity(bookApiDto);
        bookService.createBook(book);
        bookApiDto.setPrice(book.getPrice());
        return new ResponseEntity<BookApiDTO>(bookApiDto,HttpStatus.CREATED);
    }

    private Book convertBookApiDtoToEntity(BookApiDTO bookApiDTO){
        Book book = new Book();
        book.setYear(bookApiDTO.getPublishDate());
        book.setTitle(bookApiDTO.getTitle());
        book.setSubtitle(bookApiDTO.getSubtitle());
        book.setPrice(generateRandomPrice());

        List<String> publishers = bookApiDTO.getPublishers();
        book.setPublisher(listToString(publishers));

        book.setPages(bookApiDTO.getNumberOfPages());
        book.setIsbn(bookApiDTO.getIsbn());

        List<String> authors = bookApiDTO.getPublishers();
        book.setAuthor(listToString(authors));

        book.setImage("");
        return book;
    }

    private float generateRandomPrice(){
        double random =  (500 + Math.random() * (20000 - 500));
        return Math.round(random);
    }

    private BookApiDTO convertEntityToBookApiDto(Book book){
        BookApiDTO bookApiDTO = new BookApiDTO();
        bookApiDTO.setIsbn(book.getIsbn());
        bookApiDTO.setTitle(book.getTitle());
        bookApiDTO.setSubtitle(book.getSubtitle());
        bookApiDTO.setPublishDate(book.getYear());
        bookApiDTO.setNumberOfPages(book.getPages());
        bookApiDTO.setPrice(book.getPrice());

        List<String> authors = new ArrayList<String>(Arrays.asList(book.getAuthor().split(",")));
        bookApiDTO.setAuthors(authors);

        List<String> publishers = new ArrayList<String>(Arrays.asList(book.getPublisher().split(",")));
        bookApiDTO.setPublishers(publishers);

        return bookApiDTO;
    }

    private String listToString(List<String> list){
        StringBuilder sb = new StringBuilder();
        for(String word : list){
            sb.append(word).append(",");
        }

        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}

package com.itzsrv.controller;

import com.itzsrv.model.Book;
import com.itzsrv.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/library-api/books")
public class BookController {

    @Autowired
    BookRepository bookRepository;

    @GetMapping
    List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    @GetMapping(value = "{bookId}")
    Book getBook(@PathVariable(value = "bookId") int bookId){
        return bookRepository.findById(bookId).get();
    }

    @PostMapping
    Book createBook(@RequestBody Book book){
        return bookRepository.save(book);
    }

    @PutMapping
    Book updateBook(@RequestBody Book book) throws FileNotFoundException {
        if(book == null || book.getId() == null){
            throw new FileNotFoundException("Book or book id must not be null!");
        }

        Optional<Book> optionalBook = bookRepository.findById(book.getId());

        if(optionalBook.isEmpty()){
            throw new FileNotFoundException("Book not found in the records!");
        }

        Book existingBook = optionalBook.get();
        existingBook.setName(book.getName());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setSummary(book.getSummary());
        existingBook.setRating(book.getRating());

        return bookRepository.save(existingBook);
    }

    @DeleteMapping(value = "{bookId}")
    public void deleteBookById(@PathVariable(value = "bookId") Integer bookId) throws FileNotFoundException {
        if(!bookRepository.findById(bookId).isPresent()){
            throw new FileNotFoundException("book with bookId:" +bookId+ " is not present!");
        }

        bookRepository.deleteById(bookId);
    }
}

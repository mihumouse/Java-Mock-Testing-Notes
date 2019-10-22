package com.bss.util;

import java.util.List;

import com.bss.model.Book;
import com.bss.model.BookShelf;

public class BookUtil {
    /**
     * put all books into shelf
     * @param shelf    shelf
     * @param bookList books
     */
    public void putBooksToShelf(BookShelf shelf, List<Book> bookList) {
        for (Book book : bookList) {
            shelf.addBook(book);
        }
    }
    
    public boolean isITBook(Book book) {
        return false;
    }
}
package com.bss;

import java.util.List;

import com.bss.model.Book;
import com.bss.model.BookShelf;
import com.bss.util.BookUtil;
import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VerifyMothedTest {
    
    @Test
    public void putBooksToShelfTest01() {
        // test data
        Book book1 = new Book("head first in java");
        Book book2 = new Book("thinking in java");
        Book book3 = new Book("JAVA from beginning to giving up");
        List<Book> bookList = Lists.newArrayList(book1, book2, book3);
        BookShelf shelf = Mockito.mock(BookShelf.class);
        // run target method
        BookUtil bookUtil = new BookUtil();
        bookUtil.putBooksToShelf(shelf, bookList);
        // verify
        // the method was called 3 times total
        Mockito.verify(shelf, Mockito.times(bookList.size())).addBook(ArgumentMatchers.any());
        Mockito.verify(shelf, Mockito.times(1)).addBook(
            ArgumentMatchers.argThat(book -> {
                return book.getName().equals("head first in java");
            }));
        Mockito.verify(shelf, Mockito.times(1)).addBook(
            ArgumentMatchers.argThat(book -> {
                return book.getName().equals("thinking in java");
            }));
        Mockito.verify(shelf, Mockito.times(1)).addBook(
            ArgumentMatchers.argThat(book -> {
                return book.getName().equals("JAVA from beginning to giving up");
            }));
    }
}
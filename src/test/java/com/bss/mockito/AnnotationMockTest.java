package com.bss.mockito;

import static org.mockito.ArgumentMatchers.anyInt;

import com.bss.model.Book;
import com.bss.util.BookPrinter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationMockTest {
    // target test class
    @InjectMocks
    BookPrinter bookPrinter;

    // target mock class
    @Mock
    Book book;

    @Test
    public void testBookPrinter01() {
        // stub
        Mockito.when(book.getContentByPage(anyInt())).thenReturn("page content");
        // run
        int totalPrintCount = bookPrinter.printByPage(1, 5);
        // verify
        Assert.assertEquals(5,totalPrintCount);
    }
}
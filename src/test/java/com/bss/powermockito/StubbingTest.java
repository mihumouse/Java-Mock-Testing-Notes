package com.bss.powermockito;

import com.bss.model.Book;
import com.bss.util.BookPrinter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;

@RunWith(MockitoJUnitRunner.class)
public class StubbingTest {
    @InjectMocks
    BookPrinter bookPrinter;

    @Spy
    Book book;

    @Test
    public void testStubbing01() throws Exception {
        // stub

        // with the 'doReturn...when...' way, the method will not be actually call 
        // PowerMockito.doReturn("some content").when(book, "getContentByPage", ArgumentMatchers.anyInt());

        // with the 'when...thenReturn...' way, the method will be actually call 
        PowerMockito.when(book, "getContentByPage", ArgumentMatchers.anyInt()).thenReturn("some content");

        // run
        bookPrinter.printByPage(1, 5);

        // verify
        Mockito.verify(book, Mockito.times(5)).getContentByPage(ArgumentMatchers.anyInt());
    }
}
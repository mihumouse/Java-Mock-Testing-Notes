package com.bss.mockito;

import com.bss.model.Book;
import com.bss.util.BookPrinter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class AnswerTest {

    @InjectMocks
    BookPrinter bookPrinter;
    
    @Mock
    Book book;
    
    @Test
    public void testAnswer() {
        // when called the mock class's method and didn't want it to run, and expect it do some logic, then like this
        Mockito.when(book.getContentByPage(Mockito.anyInt()))
        .then((Answer<String>) invocation -> {
            int pageNo = (int)invocation.getArgument(0);
            return "this is page " + pageNo;
        });

        int totalSum = bookPrinter.printByPage(1, 5);

        Assert.assertEquals(5, totalSum);
    }
}
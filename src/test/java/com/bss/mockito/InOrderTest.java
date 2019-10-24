package com.bss.mockito;

import com.bss.model.Book;
import com.bss.util.BookPrinter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InOrderTest {

    @InjectMocks
    BookPrinter bookPrinter;
    
    @Mock
    Book book;
    
    @Test
    public void printByPageTest01() {
        // wanted:print from 1 to 5
        bookPrinter.printByPage(1, 5);

        // verify: in 1 2 3 4 5 order
        InOrder inOrder = Mockito.inOrder(book);
        inOrder.verify(book).getContentByPage(1);
        inOrder.verify(book).getContentByPage(2);
        inOrder.verify(book).getContentByPage(3);
        inOrder.verify(book).getContentByPage(4);
        inOrder.verify(book).getContentByPage(5);

    }
}
package com.bss.powermockito;

import com.bss.model.Book;
import com.bss.util.BookPrinter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
// @RunWith(PowerMockRunner.class)
@PrepareForTest({BookPrinter.class})
// @PowerMockIgnore({"javax.management.*"})
public class VerifyMethodTest {
    @InjectMocks
    BookPrinter bookPrinter;
    
    @Mock
    Book book;
    
    @Test
    public void printByPage01() throws Exception {
        // stub
        Mockito.when(book.getContentByPage(Mockito.anyInt())).thenReturn("some content");
        
        // run
        bookPrinter.printByPage(1, 5);

        // verify: wibll throw NotAMockException, because the bookPrinter is not a mock
        PowerMockito.verifyPrivate(bookPrinter, Mockito.times(5)).invoke("print", Mockito.anyString());
    }

    @Test
    public void printByPage02() throws Exception {
        // wrap the class by spy
        BookPrinter spy = PowerMockito.spy(bookPrinter);

        // stub
        Mockito.when(book.getContentByPage(Mockito.anyInt())).thenReturn("some content");
        
        // run
        spy.printByPage(1, 5);

        // verify
        PowerMockito.verifyPrivate(spy, Mockito.times(5)).invoke("print", Mockito.anyString());
    }

    @Test
    public void testMockStubWay01() throws Exception {
        // spy by Mockito
        BookPrinter spy = Mockito.spy(bookPrinter);

        // stub by PowerMockito
        // Excption:org.mockito.exceptions.misusing.UnfinishedStubbingException
        PowerMockito.doNothing().when(spy, "print", Mockito.anyString());
        
        // run
        spy.printByPage(1, 5);

        // verify
        PowerMockito.verifyPrivate(spy, Mockito.times(5)).invoke("print", Mockito.any());
    }

    @Test
    public void testMockStubWay02() throws Exception {
        // spy by PowerMockito
        BookPrinter spy = PowerMockito.spy(bookPrinter);

        // stub by PowerMockito
        // run well
        PowerMockito.doNothing().when(spy, "print", Mockito.anyString());
        
        // run
        spy.printByPage(1, 5);

        // verify
        PowerMockito.verifyPrivate(spy, Mockito.times(5)).invoke("print", Mockito.any());
    }
}
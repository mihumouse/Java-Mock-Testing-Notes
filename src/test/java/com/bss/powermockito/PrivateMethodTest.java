package com.bss.powermockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.bss.util.BookPrinter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
// @RunWith(MockitoJUnitRunner.class)
public class PrivateMethodTest {
    @InjectMocks
    BookPrinter bookPrinter;

    @Test
    public void getNumberOfWordsTest01()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // test the private method of BookPrinter with 'PowerMockito.method'
        Method method = PowerMockito.method(BookPrinter.class, "getNumberOfWords", String.class);

        // run the 'method' with reflect way, and got the return
        int wordsNumber = (int)method.invoke(bookPrinter, "Far far away, there is a hero names MZJ");

        // verify as before
        Assert.assertEquals(9, wordsNumber);
    }
}
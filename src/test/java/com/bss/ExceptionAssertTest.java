package com.bss;

import com.bss.model.Book;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionAssertTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * 期望抛出异常
     */
    @Test
    public void BookTest01() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("the page number must greater than 0");
        Book book = new Book();
        book.setPageNum(-1);
    }

    /**
     * 期望无异常
     */
    @Test
    public void BookTest02() {
        boolean isException = false;
        Book book = new Book();
        try {
            book.setPageNum(100);
        } catch (Exception e) {
            isException = true;
        }
        Assert.assertFalse(isException);
    }
}
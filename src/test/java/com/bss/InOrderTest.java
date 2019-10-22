package com.bss;

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
        // 打印第1-5页
        bookPrinter.printByPage(1, 5);

        // 验证打印时按照1、2、3、4、5页的顺序获取内容
        InOrder inOrder = Mockito.inOrder(book);
        inOrder.verify(book).getContentByPage(1);
        inOrder.verify(book).getContentByPage(2);
        inOrder.verify(book).getContentByPage(3);
        inOrder.verify(book).getContentByPage(4);
        inOrder.verify(book).getContentByPage(5);

    }
}
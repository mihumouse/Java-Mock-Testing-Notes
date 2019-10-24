package com.bss.mockito;

import java.util.List;

import com.bss.model.Book;
import com.bss.model.BookShelf;
import com.bss.util.BookUtil;
import com.google.common.collect.Lists;

import org.junit.Assert;
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

        // mock
        BookShelf shelf = Mockito.mock(BookShelf.class);
        
        // run target method
        BookUtil bookUtil = new BookUtil();
        bookUtil.putBooksToShelf(shelf, bookList);
        // verify
        // the method was called 3 times total
        Mockito.verify(shelf, Mockito.times(bookList.size())).addBook(ArgumentMatchers.any());
        Mockito.verify(shelf, Mockito.times(1)).addBook(ArgumentMatchers.argThat(book -> {
            return book.getName().equals("head first in java");
        }));
        Mockito.verify(shelf, Mockito.times(1)).addBook(ArgumentMatchers.argThat(book -> {
            return book.getName().equals("thinking in java");
        }));
        Mockito.verify(shelf, Mockito.times(1)).addBook(ArgumentMatchers.argThat(book -> {
            return book.getName().equals("JAVA from beginning to giving up");
        }));
    }

    @Test
    public void getBooksTest01() {
        // test data
        Book book1 = new Book("head first in java");
        Book book2 = new Book("thinking in java");
        Book book3 = new Book("JAVA from beginning to giving up");
        BookShelf shelf = new BookShelf("IT_001");
        shelf.addBook(book1).addBook(book2).addBook(book3);

        // run:test getBook() method
        List<Book> bookList = shelf.getBooks();
        // 视业务情况验证属性，本例仅用name验证
        boolean result0 = bookList.get(0).getName().equals("head first in java");
        boolean result1 = bookList.get(1).getName().equals("thinking in java");
        boolean result2 = bookList.get(2).getName().equals("JAVA from beginning to giving up");
        Assert.assertTrue(result0);
        Assert.assertTrue(result1);
        Assert.assertTrue(result2);
        Assert.assertEquals(3, bookList.size());
    }

    @Test
    public void getBooksTest02() {
        // test data
        Book book1 = new Book("thinking in java");
        Book book2 = new Book("thinking in java");
        Book book3 = new Book("JAVA from beginning to giving up");
        BookShelf shelf = new BookShelf("IT_001");
        // 假设书并不是按照放入顺序进行存储的
        shelf.addBook(book1).addBook(book2).addBook(book3);

        // run:test getBook() method
        List<Book> bookList = shelf.getBooks();

        // 期望获取3调记录
        Assert.assertEquals(3, bookList.size());
        boolean flag0 = true;
        boolean flag1 = true;
        boolean flag2 = true;

        for (int i = bookList.size() - 1; i >= 0; i--) {
            Book book = bookList.get(i);

            // 视业务情况验证属性，本例仅用name验证
            boolean result0 = flag0 && book.getName().equals("thinking in java");
            boolean result1 = flag1 && book.getName().equals("thinking in java");
            boolean result2 = flag2 && book.getName().equals("JAVA from beginning to giving up");

            // 对验证通过的标志设置为不必验证，同时从list中删除已验证的结果
            if (result0) {
                flag0 = false;
                bookList.remove(i);
                continue;
            }
            if (result1) {
                flag1 = false;
                bookList.remove(i);
                continue;
            }
            if (result2) {
                flag2 = false;
                bookList.remove(i);
                continue;
            }
        }
        // 如果集合中无数据，则证明全部通过了验证
        Assert.assertEquals(0, bookList.size());
    }
}
package com.bss;

import java.util.List;

import com.bss.model.Book;
import com.bss.util.BookUtil;

import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ArgumentMatherTest {

    /**
     * List的ArgumentMatchers尝试
     */
    public static void testArgumentMather01() {
        List list = Mockito.mock(List.class);

        // any int value return "hello"
        Mockito.when(list.get(ArgumentMatchers.anyInt())).thenReturn("hello");
        
        // do some logic with "argThat": if greater than 3,then throw exception
        // 此处会报错，基本类型无法转成需要操作的对象
        // Mockito.when(
        //         list.get(ArgumentMatchers.argThat(
        //             (ArgumentMatcher<Integer>) n -> { return n > 3;}))
        //         )
        //         .thenReturn("hello");

        System.out.println(list.get(2));
        System.out.println(list.get(5));
    }

    /**
     * 自定义对象的ArgumentMatchers尝试
     */
    public static void testArgumentMather02() {
        BookUtil bookUtil = Mockito.mock(BookUtil.class);
        
        Mockito.when(bookUtil.isITBook(ArgumentMatchers.argThat(book -> {
            if(book.getName().indexOf("java") > -1) {
                return true;
            } else {
                return false;
            }
        }))).thenReturn(true);
        
        // 不能同时用两个ArgumentMatchers
        // Mockito.when(bookUtil.isITBook(ArgumentMatchers.any())).thenReturn(false);

        Book book1 = new Book();
        book1.setName("Thinking in java");
        System.out.println(book1.getName() + " :" + bookUtil.isITBook(book1));
        Book book2 = new Book();
        book2.setName("some book");
        System.out.println(book2.getName() + " :" + bookUtil.isITBook(book2));
    }

    public static void main(String [] args) {
        testArgumentMather01();
        testArgumentMather02();
    }
}
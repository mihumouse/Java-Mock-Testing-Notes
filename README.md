# Java-Mock-Testing-Notes
## 目录
- [Java-Mock-Testing-Notes](#java-mock-testing-notes)
  - [目录](#%e7%9b%ae%e5%bd%95)
  - [Mockito](#mockito)
    - [Import Mocktio](#import-mocktio)
    - [Hello Mockito](#hello-mockito)
    - [Stubbing](#stubbing)
    - [Argument mathers](#argument-mathers)
    - [Mock by annotation](#mock-by-annotation)
    - [Verify](#verify)
## Mockito
![image text](https://raw.githubusercontent.com/mihumouse/Java-Mock-Testing-Notes/master/media/img/mockito%40logo%402x.png)

[Mockito javadoc](https://raw.githubusercontent.com/Snailclimb/JavaGuide/master/README.md):Mockito2.X版本的在线文档及案例，组件的整体细节，建议查阅在线文档。本文档整理实际测试中常用场景。

### Import Mocktio

- 通过[maven库](https://mvnrepository.com/artifact/org.mockito/mockito-core/)获取Mockito坐标。如：
```
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>2.28.2</version>
    <scope>test</scope>
</dependency>
```
注：如果结合PowerMockito使用，需要考虑PowerMockito是否能与Mockito某版本集成，如Mockito3.0.0，可能会不被支持。

- 结合Junit使用，另增加：

```
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
```

### Hello Mockito

```
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HelloMockTest {

    @Test
    public void test01() {
        // 1.mock List interface
        List mockList = Mockito.mock(List.class);
        
        // 2.stub mockList when get(0) return "Hello Mock"
        Mockito.when(mockList.get(0)).thenReturn("Hello Mock");
        
        // 3.run the mocked List's methode
        System.out.println(mockList.get(0));
        
        // 4.verify the get(0) was invoked
        Mockito.verify(mockList).get(0);
    }

```
test01演示mockito基础功能：
1. mock：对List接口进行Mock，模拟出了一个mocklis对象；
2. stub：当调用List.get(0)时，返回“Hello Mock”；
3. verify：对目标代码的执行和结果进行验证。

### Stubbing

所谓stub，即使用“桩代码”替换目标测试类依赖的代码或未被实现的代码，目的：
- [x] 隔离：确保测试不依赖外部类，不受外部类影响；
- [x] 补缺：对未实现的代码，通过桩代码“实现”；
- [x] 控制：通过桩代码提供测试过程中所需的数据；


```
    public static void testStubbing01() {
        List mockList = Mockito.mock(List.class);
        // stub        
        Mockito.when(mockList.get(0)).thenReturn("the first");

        // print "the first"
        System.out.println(mockList.get(0));
        // print "the first" too
        System.out.println(mockList.get(0));
        // print null
        System.out.println(mockList.get(2));

        Mockito.when(mockList.get(3)).thenReturn("the third");
        Mockito.when(mockList.get(3)).thenThrow(new NullPointerException());
        // throw exception
        System.out.println(mockList.get(3));
    }
```
- 打桩可以根据参数值返回具体值，也可抛出异常；
- 同一条件，打多次桩，以最后一次为准；
- 对相同条件的打桩，调用多次，返回相同值；
- 对未显式给出打桩的条件，mock会返回默认值，对象返回null，int\Integer返回0，boolean\Boolean返回false，等等；

### Argument mathers

不论stub或verify，都存在对方法中参数进行模糊或具体的匹配需求，使用ArgumentMatchers对参数进行匹配。  
如：  
1. 当List.get(n)的参数n为任意数字，都返回“hello”，或当List.get()的参数n>3时抛出数组越界异常。 
2. 当方法参数的name属性值包含“java”时，返回true，否则返回false；   

此时，需要对入参有一个比对，即使用ArgumentMathers处理。  

单元测试类：
```
@RunWith(MockitoJUnitRunner.class)
public class ArgumentMatherTest {

    /**
     * try a ArgumentMatchers01
     * for List
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
     * try a ArgumentMatchers
     * stub BookUtil.isITBook()，if Book's name contains "java", return "true"
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

结果打印如下:
hello
hello
Thinking in java :true
some book :false
```
用例中相关类：
```
public class BookUtil {
    public boolean isITBook(Book book) {
        // to do
        return false;
    }
}
```
```
public class Book {
    private String name;
    private String auther;
    private String publishDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuther() {
        return auther;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
}
```
### Mock by annotation
Spring下的类通常以@Resource方式注入多个对象，测试此类时，Mockito.mock()的方式可以以注解写法替代，并完成注入，如下：  
@InjectMocks：需要注入的类；  
@Mock：mock并注入给InjectMocks注解的类；

目标测试类为BookPrinter，运行时，注解将Mock一个Book对象，注入到BookPrinter中的book变量。    
目标测试类：
```
public class BookPrinter {
    @Resource
    Book book;

    /**
     * print content between startPageNum and endPageNum
     * @param beginPageNum 
     * @param endPageNum
     * @return total print count
     */
    public int printByPage(int beginPageNum, int endPageNum) {
        int totalPrintCount = 0;
        int printNum = beginPageNum;
        while(printNum <= endPageNum) {
            print(book.getContentByPage(printNum));
            printNum++;
            totalPrintCount++;
        }
        return totalPrintCount;
    }

    private void print(String content) {
        System.out.println(content);
    }
}
```
单元测试类：
```
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
```
### Verify
测试最终目的为验证结果正确性，mock、stub是为了解决目标测试程序对外部的依赖，verify则为验证数据、逻辑正确而存在。  
常有几种verify的场景：  
1. 方法返回单一值验证，可直接Assert.assertEquals(结果值, 目标值)，布尔型Assert.assertTrue(结果值)；
```
Assert.assertEquals(5,totalPrintCount);
```
2. 方法返回集合数据验证，需验证总数以及每条数据每个属性，用多个assertTrue()，确保出错快速定位；
```
@RunWith(MockitoJUnitRunner.class)
public class VerifyMothedTest {
    @Test
    public void getBooksTest01() {
        // test data
        Book book1 = new Book("head first in java");
        Book book2 = new Book("thinking in java");
        Book book3 = new Book("JAVA from beginning to giving up");
        BookShelf shelf = new BookShelf("IT_001");
        shelf.addBook(book1)
             .addBook(book2)
             .addBook(book3);

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
}
```
如果存在集合中数据可能重复且数据的顺序不能确定的情况，最好校验一条删除一条，全部校验完毕后判断集合中再无数据，以严格的确保验证准确性。  
```
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
```
3. 异常验证：分期望无异常、期望有异常两种情形。  
verify方法如下

注：ExpectedException务必为public

目标测试类：
```
public class Book {
    private String name;
    private String auther;
    private String publishDate;
    private Object content;
    private int pageNum;

    /**
     * book's page number must greater than 0
     */
    public void setPageNum(int pageNum) {
        if(pageNum <= 0) {
            throw new IllegalArgumentException("the page number must greater than 0");
        }
        this.pageNum = pageNum;
    }
    ......
```
单元测试类：
```
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
```

4. 有中间过程逻辑调用第三方方法，则需要验证调用其方法的次数，以及传入参数；  
如：  
目标测试类：
```
public class BookUtil {
    /**
     * put all books into shelf
     * @param shelf    shelf
     * @param bookList books
     */
    public void putBooksToShelf(BookShelf shelf, List<Book> bookList) {
        for (Book book : bookList) {
            shelf.addBook(book);
        }
    }
    ....
}
```
单元测试类:
```
public class VerifyMothedTest {
    
    @Test
    public void putBooksToShelfTest01() {
        // test data
        Book book1 = new Book("head first in java");
        Book book2 = new Book("thinking in java");
        Book book3 = new Book("JAVA from beginning to end");
        List<Book> bookList = Lists.newArrayList(book1, book2, book3);
        BookShelf shelf = Mockito.mock(BookShelf.class);

        // run target method
        BookUtil bookUtil = new BookUtil();
        bookUtil.putBooksToShelf(shelf, bookList);

        // verify
        // the method was called 3 times total
        Mockito.verify(shelf, Mockito.times(bookList.size())).addBook(ArgumentMatchers.any());、

        Mockito.verify(shelf, Mockito.times(1)).addBook(
            ArgumentMatchers.argThat(book -> {
                return book.getName().equals("head first in java");
            }));
        Mockito.verify(shelf, Mockito.times(1)).addBook(
            ArgumentMatchers.argThat(book -> {
                return book.getName().equals("thinking in java");
            }));
        Mockito.verify(shelf, Mockito.times(1)).addBook(
            ArgumentMatchers.argThat(book -> {
                return book.getName().equals("JAVA from beginning to end");
            }));
    }
}
```
# Java-Mock-Testing-Notes

## mockito

### import Mocktio

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

### Hello Mock

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
test01演示mockito的基础功能：
1. mock：对List接口进行Mock，模拟出了一个mocklis对象；
2. stub：当调用List.get(0)时，返回“Hello Mock”；

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

不论stub或verify，都存在对方法中参数进行模糊或具体的匹配需求。
如：当List.get(n)的参数n为任意数字，都返回“hello”，或当List.get()的参数n>3时抛出数组越界异常。
此时，需要对入参有一个比对，即使用ArgumentMathers处理。

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

print:
hello
hello
Thinking in java :true
some book :false
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

```
public class BookUtil {
    public boolean isITBook(Book book) {
        // to do
        return false;
    }
}
```
### mock by annotation
Mockito.mock()的方式可以以注解写法替代，如下：
目标测试类为BookPrinter，运行时，注解将Mock一个Book对象，注入到BookPrinter中的book变量。

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
BookPrinter类：
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
### Verify
测试最终目的为验证结果正确性，mock、stub是为了解决目标测试程序对外部的依赖，verify则为验证数据而存在。
存在几种verify的场景：
1. 单一值验证，可直接Assert.assertEquals(结果值, 目标值)，布尔型Assert.assertTrue(结果值)；
```
Assert.assertEquals(5,totalPrintCount);
```
2. 集合数据验证，需验证总数以及每条数据每个属性，用多个assertTrue()；
3. 异常验证：分期望无异常、期望有异常两种情形，verify方法如下
注：ExpectedException务必为public

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
```
public class Book {
    private String name;
    private String auther;
    private String publishDate;
    private Object content;
    private int pageNum;

    public int getPageNum() {
        return pageNum;
    }
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
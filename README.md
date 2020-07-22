# Java-Mock-Testing-Notes
Mock测试解决的问题：构建模拟类，避免测试依赖外部类；构造期望数据，方便用例数据产生；提供验证机制，简化运行结果核对。  
## 目录
- [Java-Mock-Testing-Notes](#java-mock-testing-notes)
  - [目录](#目录)
  - [Mockito](#mockito)
    - [Setup Mocktio](#setup-mocktio)
    - [Hello Mockito](#hello-mockito)
    - [Stubbing](#stubbing)
    - [Argument mathers](#argument-mathers)
    - [Mock by annotation](#mock-by-annotation)
    - [Verify](#verify)
    - [Spy](#spy)
    - [Answer](#answer)
    - [In Order](#in-order)
  - [PowerMockito](#powermockito)
    - [Setup PowerMock](#setup-powermock)
    - [Test private method](#test-private-method)
    - [Stubbing](#stubbing-1)
    - [Verify](#verify-1)
    - [About @PrepareForTest](#about-preparefortest)
    - [About @RunnWith](#about-runnwith)
    - [About @PowerMockIgnore](#about-powermockignore)
  - [Some puzzles](#some-puzzles)
    - [About variable parameters](#about-variable-parameters)
    - [About Supperclass](#about-supperclass)
    - [Mock private inner class](#mock-private-inner-class)
    - [Inject by type](#inject-by-type)
    - [Unified the way on mock/spy and stub](#unified-the-way-on-mockspy-and-stub)
    - [update jdk and mock version](#update-jdk-and-mock-version)
  - [Some summary of unit testing](#some-summary-of-unit-testing)
## Mockito
![image text](https://raw.githubusercontent.com/mihumouse/Java-Mock-Testing-Notes/master/media/img/mockito%40logo%402x.png)

[Mockito javadoc online](https://javadoc.io/doc/org.mockito/mockito-core/2.10.0/org/mockito/Mockito.html)：Mockito2.X版本的在线文档及案例，组件的整体细节，建议查阅在线文档。本文档整理实际测试中常用场景。

### Setup Mocktio

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
mockito基础功能：
1. mock：对List接口进行Mock，模拟出了一个mocklis对象（注：mock行为对interface、class均有效）；
2. stub：当调用List.get(0)时，返回“Hello Mock”；
3. verify：对目标代码的执行和结果进行验证。  

后续进行各项详述。

### Stubbing

所谓stub，即使用“桩代码”替换目标测试类依赖的代码或未被实现的代码，目的：
- 隔离：确保测试不依赖外部类，不受外部类影响；
- 补缺：对未实现的代码，通过桩代码“实现”；
- 控制：通过桩代码提供测试过程中所需的数据。


```
@RunWith(MockitoJUnitRunner.class)
public class StubbingTest {
    @Test
    public void testStubbing01() {
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
}
```
- 打桩可以根据参数值返回具体值，也可抛出异常；
- 同一条件，打多次桩，以最后一次为准；
- 对相同条件的打桩，调用多次，返回相同值；
- 对未显式给出打桩的条件，mock会返回默认值，对象返回null，int\Integer返回0，boolean\Boolean返回false，等等；

### Argument mathers

不论stub或verify，都存在对方法中参数进行模糊或具体的匹配需求，使用ArgumentMatchers对参数进行匹配。  
如：  
1. 当List.get(n)的参数n为任意数字，都返回“hello”，或当List.get()的参数n>3时抛出数组越界异常;
2. 当BookUtil.isITBook(String name)的参数的name属性值包含“java”时，返回true，否则返回false。      

此时，需要对入参有一个匹配行为，即使用ArgumentMathers。  

目标测试类：
```
public class BookUtil {
    public boolean isITBook(Book book) {
        // to do
        return false;
    }
}

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
    ...
}
```

单元测试类：
```
@RunWith(MockitoJUnitRunner.class)
public class ArgumentMatherTest {

    /**
     * try a ArgumentMatchers for List
     */
    @Test
    public void testArgumentMather01() {
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
    @Test
    public void testArgumentMather02() {
        BookUtil bookUtil = Mockito.mock(BookUtil.class);
        
        Mockito.when(bookUtil.isITBook(ArgumentMatchers.argThat(book -> {
            if(book.getName().indexOf("java") > -1) {
                return true;
            } else {
                return false;
            }
        }))).thenReturn(true);
        
        // Don't use more than one ArgumentMatchers on the same method like this
        // Mockito.when(bookUtil.isITBook(ArgumentMatchers.any())).thenReturn(false);

        Book book1 = new Book();
        book1.setName("Thinking in java");
        System.out.println(book1.getName() + " :" + bookUtil.isITBook(book1));
        Book book2 = new Book();
        book2.setName("some book");
        System.out.println(book2.getName() + " :" + bookUtil.isITBook(book2));
    }
}

结果打印如下:
hello
hello
-------------------
Thinking in java :true
some book :false
```
注：stub或verify如果有多个参数，那么你需要注意，多个参数要么全用argument matcher，要么全不用argument matcher。anyInt(), anyString(), eq()等写法，也属于argument matcher。

### Mock by annotation
业务开发中，Spring下的类通常以@Resource方式注入多个对象，测试此类时，Mockito.mock()的方式可以以注解写法替代，并完成注入，如下：  
@InjectMocks：被注入的类、需测试的类；  
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
单元测试大致有几种结果验证的场景，直接的数据验证，不需使用Mockito的verify API，逻辑的验证则需要。  
常用场景： 
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
3. 异常验证：分期望无异常、期望有异常两种情形。verify方法如下:  
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

4. 有中间过程逻辑调用其他方法，则需要验证调用其方法的次数，以及传入参数，使用Mockito的verify的API；  
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

        // mock
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
### Spy
 当依赖的类真的需要执行时，如：不想stub造数据或某段代码为历史遗留代码，那可以用Spy方式“真调用”。  
 - spy后，默认为真调用，但也可以stub;  
 - 需要使用spy后的对象，而不能使用原对象；
 - spy需要谨慎使用，不应该经常用到spy。
 ```
@RunWith(MockitoJUnitRunner.class)
public class SpyTest {

    @Test
    public void spyTest() {
        List list = new LinkedList();
        // with spy, calls "real" methods
        List spy = Mockito.spy(list);

        // you can stub some method which you don't want spy
        Mockito.when(spy.size()).thenReturn(100);

        // using the spy calls "real" methods
        spy.add("one");
        spy.add("two");

        // prints "one" - the first element of a list
        System.out.println(spy.get(0));

        // because 'size()' was stubbed, so 100 is printed
        System.out.println(spy.size());

        // optionally, you can verify
        Mockito.verify(spy).add("one");
        Mockito.verify(spy).add("two");
    }
}
 ```
### Answer
有一种场景：当你要测试的函数，处理过程中依赖其他mock类的函数（公共方法、工具类等），该函数正常业务下会多数据进行更新，但由于mock导致其不被执行（除特殊情况，也不应该执行），但为了测试能走下去，仍需执行一些必要的更新，此时需要用answer认为定义更新逻辑。如下：   
预测试BookPrinter类的printByPage方法，但不想执行book.getContentByPage(printNum)：
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
        // total page
        int totalPrintCount = 0;
        // total words
        int totalWords = 0; 

        int printNum = beginPageNum;
        while(printNum <= endPageNum) {
            String currentPageContent = book.getContentByPage(printNum);
            print(currentPageContent);
            printNum++;
            totalPrintCount++;
            totalWords += getNumberOfWords(currentPageContent);
        }
        System.out.println("total words:" + totalWords);
        return totalPrintCount;
    }
    …… 
}

public class Book {
    protected String name;
    protected String auther;
    protected String publishDate;
    protected Object content;
    protected int pageNum;
    
    public String getContentByPage(int pageNum) {
        // to do
        if(pageNum > 1) {
            throw new NullPointerException();
        }
        return "some content";
    }
    ……
}
```
测试用例：   
```
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

结果打印如下:
this is page 1
this is page 2
this is page 3
this is page 4
this is page 5
total words:20
```
通过invocation获取在测试程序运行时的实际参数值，并进行期望的加工、处理，确保后续测试的正常进行。
### In Order
当期望目标测试逻辑存在时序需求时，需要用InOrder进行验证。  
如：验证打印顺序是否正确。  
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

```
public class Book {
    private String name;
    private String auther;
    private String publishDate;
    private Object content;
    private int pageNum;
    
    public String getContentByPage(int pageNum) {
        // to do
        return "some content";
    }
    ....
```
单元测试类

```
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
```
 ## PowerMockito
 PowerMockito扩展了Mockito的短板，使用字节码操作（Mockito主要为代理机制），实现了private、static、final等方法的调用。  
 ### Setup PowerMock
 同Mockito方式获取坐标。由于PowerMockito会调用Mockito内部API，需注意两个组件见的版本兼容性。
 ```
     <dependency>
        <groupId>org.powermock</groupId>
        <artifactId>powermock-module-junit4</artifactId>
        <version>2.0.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.powermock</groupId>
        <artifactId>powermock-api-mockito2</artifactId>
        <version>2.0.2</version>
        <scope>test</scope>
    </dependency>
 ```

### Test private method
当业务类中抽取了private方法时，使用Mockito无法执行，需要借助PowerMockito。  
如下类，包含一个统计字符串中字数的私有方法，在单元测试中，应单独设计用例测试：    
```
public class BookPrinter {
    ...    
    /**
     * count the words number of the content
     * @param content content
     * @return total number
     */
    private int getNumberOfWords(String content) {
        return null == content? 0 : content.split(" ").length;
    }
    ...
}
```
单元测试类：  
注：@RunWith(PowerMockRunner.class)、@RunWith(MockitoJUnitRunner.class) 在此均可正常运行，必须用MockitoJUnitRunner的后续说明。  
```
// @RunWith(PowerMockRunner.class)
@RunWith(MockitoJUnitRunner.class)
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
```
为简化上述使用，可建立单元测试公共工具方法，如下：  
注：方法不支持参数传null
```
public class MockUtil {

    /**
     * common method of calling private method
     * @param <T>         target test class
     * @param instance    target test instance of class
     * @param methodName  tartet private method
     * @param paras       parameters of the private method
     * @return private method's return
     */
    public static <T extends Object> Object runPrivateMethod(T instance, String methodName, Object... paras) {
        Class[] paraTypes = new Class[paras.length];
        for (int i = 0; i < paras.length; i++) {
            paraTypes[i] = paras[i].getClass();
        }

        Class clazz = instance.getClass();
        Method method = PowerMockito.method(clazz, methodName, paraTypes);
        
        Object returnObj = null;
        try {
            returnObj = method.invoke(instance, paras);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            try {
                throw e.getCause();
            } catch (Throwable e1) {
                e1.printStackTrace();
            }
        }
        return returnObj;
    }
}
```   
### Stubbing
与Mockito异曲同工，稍有差异，同样扩展了私有方法的支持。  
stub有一处坑：PowerMock提供了两种stub方式：doReturn...when...，when...thenReturn...  
当对象为mock时，stub的方法均不会被真正调用代码，当采用的spy方式时：  
- doReturn...when...：不会实际调用方法；
- when...thenReturn...：会实际调用方法，但是会按照stub的设置的返回值返回数据，而不因执行了代码而返回代码运行结果。  

坑在哪里？  
当你stub的方法没有完成或未经过测试或你根本不想测试时，“when...thenReturn...”的方式却将其运行了，可能导致报错，你却觉得stub怎么会执行？莫名其妙！

```
@RunWith(MockitoJUnitRunner.class)
public class StubbingTest {
    @InjectMocks
    BookPrinter bookPrinter;

    @Spy
    Book book;

    @Test
    public void testStubbing01() throws Exception {
        // stub

        // with the 'doReturn...when...' way, the method will not be actually call 
        // PowerMockito.doReturn("some content").when(book, "getContentByPage", ArgumentMatchers.anyInt());

        // with the 'when...thenReturn...' way, the method will be actually call 
        PowerMockito.when(book, "getContentByPage", ArgumentMatchers.anyInt()).thenReturn("some content");

        // run
        bookPrinter.printByPage(1, 5);

        // verify
        Mockito.verify(book, Mockito.times(5)).getContentByPage(ArgumentMatchers.anyInt());
    }
}
```
### Verify
verify上同Mockito仍是异曲同工，略有不同。如私有的verify：

单元测试类：  
测试bookPrinter打印1至5页，需验证调用了自有的私有print(anyIt()))方法5次。当然，也可以校验调用了1次print(1)、1次print(2)……更为严谨。  
用例代码：
```
@RunWith(MockitoJUnitRunner.class)
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
}
```
上段代码编译无问题，但执行会抛出异常：  
```
org.mockito.exceptions.misusing.NotAMockException: 
Argument passed to verify() is of type BookPrinter and is not a mock!
Make sure you place the parenthesis correctly!
```
此处引出一个mock原理的讨论：不论stub、verify，关键在于目标对象是经Mockito\PowerMockito构建出来的对象，构建形式可以是spy或mock，只有它构建出来的，它才会有“监视”的可能，从而实现stub、verify的目的。 

spy后的的单元测试类：
```
@RunWith(PowerMockRunner.class)
@PrepareForTest({BookPrinter.class})
public class VerifyMethodTest {
    @InjectMocks
    BookPrinter bookPrinter;
    
    @Mock
    Book book;

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
}
```

### About @PrepareForTest
Verify代码有一处类注解（见VerifyMethodTest.printByPage02()用例）——@PrepareForTest({BookPrinter.class})  
该注解在PowerMockito中扩展测试final、private、static方法起主要作用，可谓欲测private，必先PrepareForTest。  
说白了就是增加此注解，测试用例执行前，会将注解中的class提前摸底，搞清楚都有什么方法，便于后续执行。  
忘记添加，会报：
```
Stack trace:
org.mockito.exceptions.misusing.UnfinishedVerificationException: 
Missing method call for verify(mock) here:
-> at com.bss.powermockito.VerifyMethodTest.printByPage02(VerifyMethodTest.java:50)
Example of correct verification:
    verify(mock).doSomething()
Also, this error might show up because you verify either of: final/private/equals()/hashCode() methods.
Those methods *cannot* be stubbed/verified.
Mocking methods declared on non-public parent classes is not supported.
```

### About @RunnWith
关于测试的执行器，@RunWith(PowerMockRunner.class)、@RunWith(MockitoJUnitRunner.class)的选用，建议优先使用MockitoJUnitRunner。  
MockitoJUnitRunner已经可满足大多数场景，很多时候是由于类设计的不合理，倒逼你使用PowerMockRunner进行静态资源的测试，且容易出现莫名的问题。   

### About @PowerMockIgnore
网上对PowerMockIgnore的解释只言片语，直白粗浅的解释：      
1.PowerMock工作原理即使用自定义类加载器来加载被修改过的类，实现打桩和验证；   
2.有的程序十分喜欢蹭当前线程的类加载器把自己给加载了；   
3.根据“1、2”，可能导致本该在一个加载器的几个类被拆散（可能报ClassCastException异常），还兴许一个类在多个加载器加载了（可能报LinkageError）；   
不管怎么地吧，那开发者就要显式的告诉mock的classLoder：加载的时候忽略某些包吧，它们是有自己的归宿的（比如系统加载器），从而解决矛盾，如：@PowerMockIgnore({"javax.xml.\*"})

## Some puzzles
### About variable parameters
测试偶尔会遇到方法参数为可变参数，在reflect处理上需捎加注意，否则遇到IllegalArgumentException:wrong number of arguments等错误。  
假设有一个方法是private的不定长参数的，目的是连接多个字符串，返回结果（仅为举例，无其他校验） 。   
```
public class StringUtil {
    /**
     * connect strings by StringBuilder
     * @param strings
     * @return result of connect strings
     */
    private String con(String... ss) {
        StringBuilder result = new StringBuilder();
        for (String s : ss) {
            result.append(s);
        }
        return result.toString();

    }
}
```
单元测试用例：   
```
@RunWith(MockitoJUnitRunner.class)
public class VariableParametersTest {

    @InjectMocks
    private StringUtil stringUtil;

    @Test
    public void testVarableParameterMethod()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = PowerMockito.method(StringUtil.class, "con", String[].class);
        String result = (String)method.invoke(stringUtil, new String[]{"a", "b", "c"});
        Assert.assertEquals("abc", result);
    }
}
```
虽然不定长参数本质是一个数组，但当反射invoke传入数组时，仍会报错：   
```
java.lang.IllegalArgumentException: wrong number of arguments
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.bss.powermockito.VariableParametersTest.testVarableParameterMethod(VariableParametersTest.java:25)
```
数组前加强转，passed：
```
        Method method = PowerMockito.method(StringUtil.class, "con", String[].class);
        String result = (String)method.invoke(stringUtil, (Object)new String[]{"a", "b", "c"});
```
### About Supperclass
Mock的应用过程，有一种场景不被支持：在测试类存在父类，且业务需要使用父类的属性，同时使用了@RunWith(PowerMockRunner.class)、@PrepareForTest，则父类属性不会被注入到测试类中。
父子类如下（一段仅为举例的代码）：   
```
public class Book {
    protected String name;
    protected String auther;
    protected Object content;
    ...
}

public class Ebook extends Book {
    private int binarySize;
    ...
}
```
单元测试用例：   
```
@RunWith(PowerMockRunner.class)
@PrepareForTest({Ebook.class})
public class SuperclassFieldInjectTest {
    @InjectMocks
    Ebook ebook;

    @Mock
    Object content;

    @Test
    public void testSupperclassFieldInject() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        // at first, the ebook's content is null, because of @PrepareForTest
        System.out.println("content:" + content);
        System.out.println("ebook's content:" + ebook.getContent());

        // bindings by reflect
        Field field = ebook.getClass().getSuperclass().getDeclaredField("content");
        field.setAccessible(true);
        field.set(ebook, content);

        // then ebook's content had the reference you want
        System.out.println("content:" + content);
        System.out.println("ebook's content:" + ebook.getContent());
    }
}
======================================================
print:
content:Mock for Object, hashCode: 1446001495
ebook's content:null
content:Mock for Object, hashCode: 1446001495
ebook's content:Mock for Object, hashCode: 1446001495
======================================================
```
分析：主要问题在于PrepareForTest，若不使用该注解，则可正常将Mock类注入测试类中。加了PrepareForTest注解，则注不进去，应该是该注解在Prepare时偷懒了，没有向上解析父类和接口相关语义，导致注入失效（源码未读，勉强猜测）。   
故例中只好使用反射找到父类Field并人工绑定，由于属性访问权限非public，故强制setAccessible，以获取权限。  
### Mock private inner class
Mockito中mock的行为，即在测试运行的容器（或环境）中，对任意指定的class进行模拟，包括java中的任意class：static、final、inner class，最根本的mock使用，就是拿到class，反射机制可以获取任意class类型，然后将其mock。   
比如：私有内部类，常规机制是无法直接被外部访问的，如果想mock，可采用如下方式   
想测试InnerClass.hello()方法，且不依赖Inner（此处仅为例子简单而设计）
```
/**
 * test mock InnerClass
 */
public class InnerClass {
    private Inner inner;
    public String hello() {
        return "hello " + inner.getName();
    }

    private class Inner {
        private String name;

        public String getName() {
            return this.name;
        }
    }
}
```
用例编写：
```
@RunWith(PowerMockRunner.class)
@PrepareForTest({InnerClass.class})
public class InnerClassTest {
    @InjectMocks
    InnerClass innerClass;
    @Test
    public void testMockInnerClass() throws Exception {
        // stub a private inner class
        Class clazz = Class.forName("com.bss.model.InnerClass$Inner");
        Object mock = Mockito.mock(clazz);
        PowerMockito.doReturn("XiaoMing").when(mock, "getName");

        // binding the mock instance to the Inject mock class with reflect
        Field field = PowerMockito.field(InnerClass.class, "inner");
        field.set(innerClass, mock);

        // run
        String result = innerClass.hello();
        // verify
        Assert.assertEquals("hello XiaoMing", result);
    }
}
```

### Inject by type
@Mock将类模拟后，向@InjectMocks修饰的类变量注入时，是按类型寻找并匹配的，而不是按变量名（虽然写法规范开发者要保证两者一致），故当@InjectMocks修饰的类有两个同类型的类变量时，@Mock会失效(运行时期望的mock对象为null)，因为它无法聪明到懂得如何按你的意愿匹配。
思考一个问题：同一个类中存在两个同类型的变量的设计方式，是否合适？      
```
@RunWith(MockitoJUnitRunner.class)
public class AnnotationMockTest {

    @InjectMocks
    BookPrinter bookPrinter;

    // Suppose 'BookPrinter' has two field of type 'Book', and you coding like this, mockito couldn't know how to matching, because it injects by type. if you insist, 'book1' and 'book2' will be null at runtime.
    @Mock
    Book book1;

    @Mock
    Book book2;

    @Test
    public void testBookPrinter01() {
        ......
    }
}
``` 
### Unified the way on mock/spy and stub
在一些场景，存在mockito\powermockito混用的情况，但在mock\spy同时stub时，要注意组件顺序，或者说尽量统一成Powermockito，否则会收获一个异常。   
原因是PowerMockito是基于Mockito接口的封装，当后续的行为使用Powermockito时，那么前序行为也需要用PowerMockito。   
通俗说就是后面用高级货，那么前序也要用高级货；前序用低端产品，后续无法支持高端的行为。   
如下代码：   
testMockStubWay01()的spy使用Mockito，stub使用PowerMockito，运行报异常（UnfinishedStubbingException，详见代码下方）；   
testMockStubWay02()的spy、stub均使用PowerMockito，运行正常；   
```
@RunWith(PowerMockRunner.class)
@PrepareForTest({BookPrinter.class})
public class VerifyMethodTest {
    @InjectMocks
    BookPrinter bookPrinter;
    
    ......

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
```
```
Stack trace:
org.mockito.exceptions.misusing.UnfinishedStubbingException: 
Unfinished stubbing detected here:
-> at com.bss.powermockito.VerifyMethodTest.testMockStubWay01(VerifyMethodTest.java:60)
E.g. thenReturn() may be missing.
Examples of correct stubbing:
    when(mock.isOk()).thenReturn(true);
    when(mock.isOk()).thenThrow(exception);
    doThrow(exception).when(mock).someVoidMethod();
```
### update jdk and mock version
升级JDK版本后，可能出现如下底层异常，此时需要同步对mockito版本在pom.xml中进行更新。
```
Underlying exception : java.lang.UnsupportedOperationException: Cannot define class using reflection
```
## Some summary of unit testing
- 功能函数职能单一，复杂业务按行为单元拆分多个子方法，逐个子方法测试，清晰业务、简化用例复杂度、易于达到覆盖度；
- 用例函数职能单一，避免单用例覆盖多个场景，人工增加用例复杂度和后期运维成本； 
- 断言职能单一，确保测试程序执行时，能快速定位哪个预期结果存在问题；
- 用例注释完备，体现测试的场景、目的及期望结果，便于后续清晰理解用例用意；
- 用例命名：com.dce.BusiClass.methodName()类的测试类及方法应为对应test目录的com.dce.BusiClassTest.testMethodName01()、testMethodName02()、testMethodName03()等；
- 用例代码基本顺序：mock -> stub -> run -> verify
- 不论任何覆盖度级别，用例达到覆盖度无法保证业务测试充分，测试质量最终依赖对需求的理解和完善的用例(极值等特殊场景)。

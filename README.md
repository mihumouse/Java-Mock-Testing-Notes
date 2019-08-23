# Java-Mock-Testing-Notes

## mockito

### 引入Mocktio（maven环境）

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
test01演示性对mockito的基础功能进行演示：
1. mock：对List接口进行Mock，模拟出了一个mocklis对象；
2. stub：当调用List.get(0)时，返回“Hello Mock”；

所谓stub，即使用“桩代码”替换目标测试类依赖的代码或未被实现的代码，目的：
- [x] 隔离：确保测试不依赖外部类，不受外部类影响；
- [x] 补缺：对未实现的代码，通过桩代码“实现”；
- [x] 控制：通过桩代码提供测试过程中所需的数据；

### Stubbing

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
- 对相同it傲剑的打桩，调用多次，返回相同值；
- 对未显式给出打桩的条件，mock会返回默认值，对象返回null，int\Integer返回0，boolean\Boolean返回false，等等；
- 
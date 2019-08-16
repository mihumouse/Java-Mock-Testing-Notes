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
    public void name() {
        // mock List interface
        List mockList = Mockito.mock(List.class);
        // stub mockList when get(0) return "Hello Mock"
        Mockito.when(mockList.get(0)).thenReturn("Hello Mock");
        // run the mocked List's methode
        System.out.println(mockList.get(0));
        // verify the get(0) was invoked
        Mockito.verify(mockList).get(0);
    }

```
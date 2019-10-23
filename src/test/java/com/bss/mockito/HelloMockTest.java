package com.bss.mockito;

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
}
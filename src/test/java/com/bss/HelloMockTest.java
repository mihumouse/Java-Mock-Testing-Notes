package com.bss;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HelloMockTest {

    @Test
    public void name() {
        List mockList = Mockito.mock(List.class);
        Mockito.when(mockList.get(0)).thenReturn("Hello Mock");
        System.out.println(mockList.get(0));

        Mockito.verify(mockList).get(0);
    }
}
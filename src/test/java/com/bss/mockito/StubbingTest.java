package com.bss.mockito;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

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
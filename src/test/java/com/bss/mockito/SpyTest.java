package com.bss.mockito;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SpyTest {

    @Test
    public void spyTest() {
        List list = new LinkedList();
        // with spy, calls "real" methods
        List spy = Mockito.spy(list);

        // you can stub out some methods:
        Mockito.when(spy.size()).thenReturn(100);

        // using the spy calls "real" methods
        spy.add("one");
        spy.add("two");

        // prints "one" - the first element of a list
        System.out.println(spy.get(0));

        // size() method was stubbed - 100 is printed
        System.out.println(spy.size());

        // optionally, you can verify
        Mockito.verify(spy).add("one");
        Mockito.verify(spy).add("two");
    }
}
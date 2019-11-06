package com.bss.others;

import java.lang.reflect.Field;

import com.bss.model.Ebook;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
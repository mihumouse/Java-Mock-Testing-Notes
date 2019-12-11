package com.bss.others;

import java.lang.reflect.Field;

import com.bss.model.InnerClass;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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

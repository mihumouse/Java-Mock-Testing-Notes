package com.bss.powermockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.bss.util.StringUtil;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;

@RunWith(MockitoJUnitRunner.class)
public class VariableParametersTest {

    @InjectMocks
    private StringUtil stringUtil;

    @Test
    public void testVarableParameterMethod()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = PowerMockito.method(StringUtil.class, "con", String[].class);
        String result = (String)method.invoke(stringUtil, (Object)new String[]{"a", "b", "c"});
        Assert.assertEquals("abc", result);
    }
}
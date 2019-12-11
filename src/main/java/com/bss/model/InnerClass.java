package com.bss.model;

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
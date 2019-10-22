package com.bss.model;

import java.util.ArrayList;
import java.util.List;

public class BookShelf {
    private String no;
    private List<Book> bookList;

    public BookShelf(String no) {
        this.no = no;
        this.bookList = new ArrayList<>();
    }

    public void addBook(Book book) {
        bookList.add(book);
    }
}
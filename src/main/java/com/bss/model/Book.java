package com.bss.model;

public class Book {
    private String name;
    private String auther;
    private String publishDate;
    private Object content;
    private int pageNum;

    public Book() {
    }

    public Book(String name) {
        this.name = name;
    }

    public int getPageNum() {
        return pageNum;
    }
    
    /**
     * book's page number must greater than 0
     */
    public void setPageNum(int pageNum) {
        if(pageNum <= 0) {
            throw new IllegalArgumentException("the page number must greater than 0");
        }
        this.pageNum = pageNum;
    }
    
    public String getContentByPage(int pageNum) {
        // to do
        return "some content";
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuther() {
        return auther;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

}
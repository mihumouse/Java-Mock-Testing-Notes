package com.bss.util;

import javax.annotation.Resource;

import com.bss.model.Book;

public class BookPrinter {
    @Resource
    Book book;

    /**
     * print content between startPageNum and endPageNum
     * @param beginPageNum 
     * @param endPageNum
     * @return total print count
     */
    public int printByPage(int beginPageNum, int endPageNum) {
        // total page
        int totalPrintCount = 0;
        // total words
        int totalWords = 0; 

        int printNum = beginPageNum;
        while(printNum <= endPageNum) {
            String currentPageContent = book.getContentByPage(printNum);
            print(currentPageContent);
            printNum++;
            totalPrintCount++;
            totalWords += getNumberOfWords(currentPageContent);
        }
        System.out.println("total words:" + totalWords);
        return totalPrintCount;
    }
    
    /**
     * count the words number of the content
     * @param content content
     * @return total number
     */
    private int getNumberOfWords(String content) {
        return null == content? 0 : content.split(" ").length;
    }
    
    /**
     * print content
     */
    private void print(String content) {
        System.out.println(content);
    }
    
}
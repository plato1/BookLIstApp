/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abnanodegree.jk.booklistapp;

/**
 * {@Book} represents a single volume in the Google Books db. It holds the details
 * of that book such as title, first author, published date, retail price.
 */
public class Book {

    /** Title of the book */
    public final String title;
    /** name of 1st author for book */
    public final String author;
    /** date book published     */
    public final String publishedDate;
    /** retail price for book */
    public final String retailPrice;


    /**
     * Constructs a new {@link Book}.
     *
     * @param bookTitle is the title of the book
     * @param bookAuthor is 1st author of book
     * @param bookPublishedDate is date book published
     * @param bookRetailPrice  is retail price for book
     */
    public Book(String bookTitle, String bookAuthor, String bookPublishedDate, String bookRetailPrice) {
        title = bookTitle;
        author = bookAuthor;
        publishedDate = bookPublishedDate;
        retailPrice = bookRetailPrice;
    }
}

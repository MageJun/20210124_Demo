package com.dushuge.controller.eventbus;

import com.dushuge.controller.model.Audio;
import com.dushuge.controller.model.Book;
import com.dushuge.controller.model.Comic;

public class RefreshShelfCurrent {

    public Book book;
    public Comic comic;
    public Audio audio;
    public int productType;

    public RefreshShelfCurrent(int productType, Book book) {
        this.productType = productType;
        this.book = book;
    }

    public RefreshShelfCurrent(int productType, Comic comic) {
        this.productType = productType;
        this.comic = comic;
    }

    public RefreshShelfCurrent(int productType, Audio book) {
        this.productType = productType;
        this.audio = book;
    }
}

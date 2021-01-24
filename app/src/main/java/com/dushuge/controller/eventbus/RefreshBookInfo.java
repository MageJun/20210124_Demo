package com.dushuge.controller.eventbus;

import com.dushuge.controller.model.Audio;
import com.dushuge.controller.model.Book;

public class RefreshBookInfo {

    public boolean isSave;
    public Book book;
    public Audio audio;

    public RefreshBookInfo(Book book, boolean isSave) {
        this.isSave = isSave;
        this.book = book;
    }

    public RefreshBookInfo(Audio audio, boolean isSave) {
        this.isSave = isSave;
        this.audio = audio;
    }
}

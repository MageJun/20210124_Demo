package com.dushuge.controller.eventbus;

import com.dushuge.controller.model.AudioSpeedBean;

public class AudioSpeedBeanEventbus {

    public boolean isOpen = false;
    public AudioSpeedBean audioSpeedBean;

    public AudioSpeedBeanEventbus(AudioSpeedBean audioSpeedBean) {
        this.audioSpeedBean = audioSpeedBean;
    }

    public AudioSpeedBeanEventbus(AudioSpeedBean audioSpeedBean, boolean isOpen) {
        this.audioSpeedBean = audioSpeedBean;
        this.isOpen = isOpen;
    }
}

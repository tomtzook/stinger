package com.stinger.app;

import com.stinger.framework.app.appinterface.StingerApp;

public class App implements StingerApp {

    @Override
    public void install() {
        System.out.println("INSTALL APP");
    }

    @Override
    public void start() {
        System.out.println("START APP");
    }

    @Override
    public void stop() {
        System.out.println("STOP APP");
    }

    @Override
    public void uninstall() {
        System.out.println("UNINSTALL APP");
    }
}

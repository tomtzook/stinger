package com.stinger.framework.logging;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BasicFileLogger extends AbstractFileLogger {

    private final Lock mLogLock;

    public BasicFileLogger(Path path) {
        super(path);

        mLogLock = new ReentrantLock();
    }

    @Override
    protected void log(String data) {
        mLogLock.lock();
        try {
            BufferedOutputStream stream = getStream();
            stream.write(data.getBytes(StandardCharsets.UTF_8));
            stream.write('\n');
            stream.flush();

            System.err.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mLogLock.unlock();
        }
    }
}

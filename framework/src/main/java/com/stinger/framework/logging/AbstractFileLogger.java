package com.stinger.framework.logging;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

public abstract class AbstractFileLogger implements Logger, Closeable {

    private final Path mPath;

    private BufferedOutputStream mOutputStream;

    public AbstractFileLogger(Path path) {
        mPath = path;

        mOutputStream = null;
    }

    @Override
    public void info(String message, Object... args) {
        message = String.format(message, args);
        message = String.format("[%d] [INFO]: %s", System.currentTimeMillis(), message);
        log(message);
    }

    @Override
    public void error(String message, Object... args) {
        message = String.format(message, args);
        message = String.format("[%d] [ERROR]: %s", System.currentTimeMillis(), message);
        log(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);

        message = String.format("[%d] [ERROR]: %s:\n\t%s", System.currentTimeMillis(), message, stringWriter.toString());
        log(message);
    }

    @Override
    public void close() throws IOException {
        closeStream();
    }

    protected BufferedOutputStream getStream() throws IOException {
        if (mOutputStream == null) {
            mOutputStream = new BufferedOutputStream(new FileOutputStream(mPath.toFile()));
        }
        return mOutputStream;
    }

    protected void closeStream() {
        if (mOutputStream != null) {
            try {
                mOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOutputStream = null;
        }
    }

    protected abstract void log(String data);
}

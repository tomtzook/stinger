package stinger.apps.local;

import com.stinger.framework.app.appinterface.StingerApp;
import com.stinger.framework.logging.Logger;
import stinger.apps.StingerAppControl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadStingerAppControl implements StingerAppControl {

    private final StingerApp mInterface;
    private final Logger mLogger;
    private final Lock mControlLock;

    public ThreadStingerAppControl(StingerApp anInterface, Logger logger) {
        mInterface = anInterface;
        mLogger = logger;
        mControlLock = new ReentrantLock();
    }

    @Override
    public void install() {
        mControlLock.lock();
        try {
            mLogger.info("Local Thread App: install");
            mInterface.install();
        } finally {
            mControlLock.unlock();
        }
    }

    @Override
    public void start() {
        mControlLock.lock();
        try {
            mLogger.info("Local Thread App: start");
            mInterface.start();
        } finally {
            mControlLock.unlock();
        }
    }

    @Override
    public void stop() {
        mControlLock.lock();
        try {
            mLogger.info("Local Thread App: stop");
            mInterface.stop();
        } finally {
            mControlLock.unlock();
        }
    }

    @Override
    public void uninstall() {
        mControlLock.lock();
        try {
            mLogger.info("Local Thread App: uninstall");
            mInterface.uninstall();
        } finally {
            mControlLock.unlock();
        }
    }
}

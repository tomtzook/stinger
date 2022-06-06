package stinger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class StingerThreadFactory implements ThreadFactory {

    private final ThreadGroup mGroup;
    private final AtomicInteger mIndex;

    public StingerThreadFactory(ThreadGroup group) {
        mGroup = group;
        mIndex = new AtomicInteger(0);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(mGroup, r,
                "sst-" + mIndex.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    }
}

package stinger;

import java.util.concurrent.atomic.AtomicBoolean;

public class StringerControlImpl implements StingerControl {

    private final Thread mMainThread;
    private final AtomicBoolean mShutdownIndicator;

    public StringerControlImpl(Thread mainThread) {
        mMainThread = mainThread;
        mShutdownIndicator = new AtomicBoolean(false);
    }

    @Override
    public void startShutdown() {
        if (mShutdownIndicator.compareAndSet(false, true)) {
            mMainThread.interrupt();
        }
    }

    @Override
    public boolean isInShutdown() {
        return mShutdownIndicator.get();
    }
}

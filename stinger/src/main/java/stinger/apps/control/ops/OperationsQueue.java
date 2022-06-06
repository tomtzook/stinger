package stinger.apps.control.ops;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OperationsQueue {

    private final Queue<AppOperation> mQueue;
    private final Map<Integer, Integer> mQueuedApps;
    private final Lock mLock;
    private final Condition mIsNotEmpty;
    private final Condition mIsEmpty;

    public OperationsQueue() {
        mQueue = new LinkedList<>();
        mQueuedApps = new HashMap<>();
        mLock = new ReentrantLock();
        mIsNotEmpty = mLock.newCondition();
        mIsEmpty = mLock.newCondition();
    }

    public void queue(AppOperation operation) {
        mLock.lock();
        try {
            Integer count = mQueuedApps.get(operation.getId());
            //noinspection Java8MapApi
            if (count == null) {
                mQueuedApps.put(operation.getId(), 1);
            } else {
                mQueuedApps.put(operation.getId(), count + 1);
            }

            mQueue.add(operation);

            mIsNotEmpty.signalAll();
        } finally {
            mLock.unlock();
        }
    }

    public boolean queueIfNoOpQueuedOnApp(AppOperation operation) {
        mLock.lock();
        try {
            if (mQueuedApps.containsKey(operation.getId())) {
                return false;
            }

            queue(operation);
            return true;
        } finally {
            mLock.unlock();
        }
    }

    public AppOperation poll() throws InterruptedException {
        mLock.lock();
        try {
            while (mQueue.isEmpty()) {
                mIsNotEmpty.await();
            }

            AppOperation operation = mQueue.poll();
            if (operation == null) {
                return null;
            }

            int count = mQueuedApps.get(operation.getId());
            if (count > 1) {
                mQueuedApps.put(operation.getId(), count - 1);
            } else {
                mQueuedApps.remove(operation.getId());
            }

            if (mQueue.isEmpty()) {
                mIsEmpty.signalAll();
            }

            return operation;
        } finally {
            mLock.unlock();
        }
    }

    public void waitUntilEmpty(long waitTimeMs) throws InterruptedException, TimeoutException {
        mLock.lock();
        try {
            if (mQueue.isEmpty()) {
                return;
            }

            if (!mIsEmpty.await(waitTimeMs, TimeUnit.MILLISECONDS)) {
                throw new TimeoutException();
            }
        } finally {
            mLock.unlock();
        }
    }
}

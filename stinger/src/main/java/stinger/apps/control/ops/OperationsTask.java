package stinger.apps.control.ops;

import stinger.StingerControl;
import stinger.StingerEnvironment;
import stinger.apps.control.store.AppExecutionTransaction;
import stinger.apps.control.store.AppStorage;

import java.io.IOException;

public class OperationsTask implements Runnable {

    private final AppStorage mAppStorage;
    private final OperationsQueue mQueue;
    private final StingerControl mControl;

    public OperationsTask(AppStorage appStorage,
                          OperationsQueue queue,
                          StingerEnvironment environment) {
        mAppStorage = appStorage;
        mQueue = queue;
        mControl = environment.getControl();
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted() && !mControl.isInShutdown()) {
                AppOperation operation = mQueue.poll();
                if (operation == null) {
                    continue;
                }

                try {
                    try (AppExecutionTransaction transaction = mAppStorage.openForExecution(operation.getId())) {
                        switch (operation.getType()) {
                            case INSTALL:
                                transaction.install();
                                break;
                            case START:
                                transaction.start();
                                break;
                            case STOP:
                                transaction.stop();
                                break;
                            case UNINSTALL:
                                transaction.uninstall();
                                break;
                        }
                        transaction.commit();
                    }

                    if (operation.getType() == OperationType.UNINSTALL) {
                        mAppStorage.delete(operation.getId());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

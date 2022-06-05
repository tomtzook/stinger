package com.stinger.server.comm;

import com.stinger.server.Environment;
import com.stinger.framework.logging.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class CommunicationModule {

    private final ExecutorService mExecutorService;
    private final Communicator mCommunicator;

    private Future<?> mFuture;

    public CommunicationModule(ExecutorService executorService, Communicator communicator) {
        mExecutorService = executorService;
        mCommunicator = communicator;
    }

    public void start(Environment environment) {
        mFuture = mExecutorService.submit(new Task(mCommunicator, environment, environment.getLogger()));
    }

    public void stop() {
        if (mFuture != null) {
            mFuture.cancel(true);
            mFuture = null;
        }

        try {
            mCommunicator.close();
        } catch (IOException e) {

        }
    }

    private static class Task implements Runnable {

        private final Communicator mCommunicator;
        private final Environment mEnvironment;
        private final Logger mLogger;

        private Task(Communicator communicator, Environment environment, Logger logger) {
            mCommunicator = communicator;
            mEnvironment = environment;
            mLogger = logger;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    try {
                        mCommunicator.handleNextClient(mEnvironment);
                    } catch (IOException e) {
                        mLogger.error("error handling client", e);
                    }
                }
            } catch (Throwable t) {
                mLogger.error("Unexpected error in CommunicationModule", t);
            }
        }
    }
}

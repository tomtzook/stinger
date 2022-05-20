package stinger.logging;

import stinger.Constants;
import stinger.PeriodicTaskModule;
import stinger.StingerEnvironment;
import stingerlib.logging.Logger;
import stingerlib.storage.Product;
import stingerlib.storage.StorageException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class LoggingModule extends PeriodicTaskModule {

    private final LoggerControl mLoggerControl;

    public LoggingModule(ExecutorService executorService, LoggerControl loggerControl) {
        super("LoggingModule", executorService, Constants.LOGGING_CHECK_INTERVAL_MS);
        mLoggerControl = loggerControl;
    }

    @Override
    protected Runnable createTask(StingerEnvironment environment) {
        return new Task(mLoggerControl, environment, environment.getLogger());
    }

    private static class Task implements Runnable {

        private final LoggerControl mLoggerControl;
        private final StingerEnvironment mStingerEnvironment;
        private final Logger mLogger;

        private volatile long mLastRotateMs;

        private Task(LoggerControl loggerControl, StingerEnvironment stingerEnvironment, Logger logger) {
            mLoggerControl = loggerControl;
            mStingerEnvironment = stingerEnvironment;
            mLogger = logger;

            mLastRotateMs = System.currentTimeMillis();
        }

        @Override
        public void run() {
            if (mLoggerControl.getRecordCount() == Constants.LOGGING_RECORDS_ROTATE ||
                    System.currentTimeMillis() - mLastRotateMs >= Constants.LOGGING_TIME_ROTATE_MS) {
                try {
                    mLogger.info("Doing rotation");
                    Product product = mLoggerControl.rotate();
                    mLastRotateMs = System.currentTimeMillis();
                    mStingerEnvironment.getStorage().store(product);
                } catch (IOException | StorageException e) {
                    mLogger.error("LoggerModule Rotation", e);
                }
            }
        }
    }
}

package stinger.logging;

import stinger.Constants;
import stinger.modules.PeriodicTaskModule;
import stinger.StingerEnvironment;
import com.stinger.framework.logging.Logger;
import com.stinger.framework.storage.Product;
import com.stinger.framework.storage.StorageException;
import stinger.storage.StandardProductType;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

public class LoggingModule extends PeriodicTaskModule {

    private final LoggerControl mLoggerControl;

    public LoggingModule(ExecutorService executorService, LoggerControl loggerControl) {
        super("LoggingModule", executorService, Constants.LOGGING_CHECK_INTERVAL_MS);
        mLoggerControl = loggerControl;
    }

    public LoggerControl getLoggerControl() {
        return mLoggerControl;
    }

    @Override
    protected Runnable createTask(StingerEnvironment environment) {
        return new Task(mLoggerControl, environment, environment.getLogger());
    }

    private static class Task implements Runnable {

        private final LoggerControl mLoggerControl;
        private final StingerEnvironment mStingerEnvironment;
        private final Logger mLogger;

        private Task(LoggerControl loggerControl, StingerEnvironment stingerEnvironment, Logger logger) {
            mLoggerControl = loggerControl;
            mStingerEnvironment = stingerEnvironment;
            mLogger = logger;
        }

        @Override
        public void run() {
            if (mLoggerControl.getRecordCount() >= Constants.LOGGING_RECORDS_ROTATE) {
                try {
                    Optional<Product> optional = mLoggerControl.rotateIf((control)->
                            control.getRecordCount() >= Constants.LOGGING_RECORDS_ROTATE);
                    if (optional.isEmpty()) {
                        return;
                    }

                    mLogger.info("Doing rotation");
                    Product product = optional.get();
                    mStingerEnvironment.getStorage().store(
                            StandardProductType.LOG,
                            Constants.PRIORITY_LOG,
                            product);
                } catch (IOException e) {
                    mLogger.error("LoggerModule Rotation", e);
                }
            }
        }
    }
}

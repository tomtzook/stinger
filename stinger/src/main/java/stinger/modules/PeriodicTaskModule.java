package stinger.modules;

import stinger.StingerEnvironment;
import com.stinger.framework.logging.Logger;

import java.util.concurrent.ExecutorService;

public abstract class PeriodicTaskModule extends TaskModule {

    public PeriodicTaskModule(String name, ExecutorService executorService, long periodMs) {
        super(executorService, (task, environment)-> new SafePeriodicTask(name, task, environment, periodMs));
    }

    private static class SafePeriodicTask implements Runnable {

        private final String mModuleName;
        private final Runnable mWrapped;
        private final Logger mLogger;
        private final long mPeriodMs;

        private SafePeriodicTask(String moduleName, Runnable wrapped, StingerEnvironment environment, long periodMs) {
            mModuleName = moduleName;
            mWrapped = wrapped;
            mLogger = environment.getLogger();
            mPeriodMs = periodMs;
        }

        @Override
        public void run() {
            mLogger.info("Starting module %s", mModuleName);
            while (!Thread.interrupted()) {
                try {
                    mWrapped.run();
                } catch (Throwable t) {
                    mLogger.error("Error in module task", t);
                }

                try {
                    Thread.sleep(mPeriodMs);
                } catch (InterruptedException e) {
                    break;
                }
            }
            mLogger.info("Done module %s", mModuleName);
        }
    }
}

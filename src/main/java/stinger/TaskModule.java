package stinger;

import stingerlib.logging.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

public abstract class TaskModule implements Module {

    private final ExecutorService mExecutorService;
    private final BiFunction<Runnable, StingerEnvironment, Runnable> mTaskWrapper;
    private Future<?> mFuture;

    public TaskModule(ExecutorService executorService, BiFunction<Runnable, StingerEnvironment, Runnable> taskWrapper) {
        mExecutorService = executorService;
        mTaskWrapper = taskWrapper;

        mFuture = null;
    }

    public TaskModule(String name, ExecutorService executorService) {
        this(executorService, (wrapped, environment) -> new SafeTask(name, wrapped, environment));
    }

    @Override
    public synchronized void start(StingerEnvironment environment) {
        if (isRunning()) {
            return;
        }

        Runnable task = createTask(environment);
        task = mTaskWrapper.apply(task, environment);
        mFuture = mExecutorService.submit(task);
    }

    @Override
    public synchronized void stop(StingerEnvironment environment) {
        if (!isRunning()) {
            return;
        }

        if (mFuture != null) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }

    public boolean isRunning() {
        return mFuture != null;
    }

    protected abstract Runnable createTask(StingerEnvironment environment);

    private static class SafeTask implements Runnable {

        private final String mModuleName;
        private final Runnable mWrapped;
        private final Logger mLogger;

        private SafeTask(String moduleName, Runnable wrapped, StingerEnvironment environment) {
            mModuleName = moduleName;
            mWrapped = wrapped;
            mLogger = environment.getLogger();
        }

        @Override
        public void run() {
            mLogger.info("Starting module %s", mModuleName);
            try {
                mWrapped.run();
            } catch (Throwable t) {
                mLogger.error("Error in module task", t);
            } finally {
                mLogger.info("Done module %s", mModuleName);
            }
        }
    }
}

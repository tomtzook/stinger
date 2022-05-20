package stinger;

import stinger.commands.CommandQueue;
import stingerlib.logging.Logger;
import stinger.storage.Storage;

public class StingerEnvironmentImpl implements StingerEnvironment {

    private final Storage mStorage;
    private final CommandQueue mCommandQueue;
    private final Logger mLogger;
    private final StingerControl mControl;
    private final StingerModules mModules;

    public StingerEnvironmentImpl(Storage storage, CommandQueue commandQueue, Logger logger,
                                  StingerControl control, StingerModules modules) {
        mStorage = storage;
        mCommandQueue = commandQueue;
        mLogger = logger;
        mControl = control;
        mModules = modules;
    }

    @Override
    public Storage getStorage() {
        return mStorage;
    }

    @Override
    public CommandQueue getCommandQueue() {
        return mCommandQueue;
    }

    @Override
    public Logger getLogger() {
        return mLogger;
    }

    @Override
    public StingerControl getControl() {
        return mControl;
    }

    @Override
    public StingerModules getModules() {
        return mModules;
    }
}

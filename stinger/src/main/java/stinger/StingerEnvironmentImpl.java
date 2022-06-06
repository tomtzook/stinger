package stinger;

import com.stinger.framework.logging.Logger;
import stinger.meta.ToolMetaStore;
import stinger.modules.ModuleCreator;
import stinger.modules.StingerModuleImpl;
import stinger.modules.StingerModules;
import stinger.storage.Storage;

import java.util.concurrent.ExecutorService;

public class StingerEnvironmentImpl implements StingerEnvironment {

    private final ToolMetaStore mToolMetaStore;
    private final Storage mStorage;
    private final Logger mLogger;
    private final StingerControl mControl;
    private final StingerModules mModules;
    private final ModuleCreator mModuleCreator;

    public StingerEnvironmentImpl(ExecutorService executorService,
                                  ToolMetaStore toolMetaStore,
                                  Storage storage,
                                  Logger logger,
                                  StingerControl control) {
        mToolMetaStore = toolMetaStore;
        mStorage = storage;
        mLogger = logger;
        mControl = control;
        mModules = new StingerModuleImpl(this);
        mModuleCreator = new ModuleCreator(executorService, mModules);
    }

    @Override
    public ToolMetaStore getToolMetaStore() {
        return mToolMetaStore;
    }

    @Override
    public Storage getStorage() {
        return mStorage;
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

    @Override
    public ModuleCreator getModuleCreator() {
        return mModuleCreator;
    }
}

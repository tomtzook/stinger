package stinger.modules;

import stinger.StingerEnvironment;
import com.stinger.framework.logging.Logger;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class StingerModuleImpl implements StingerModules {

    private final StingerEnvironment mEnvironment;
    private final Logger mLogger;
    private final Set<InternalModule> mModules;

    public StingerModuleImpl(StingerEnvironment environment) {
        mEnvironment = environment;
        mLogger = environment.getLogger();
        mModules = new CopyOnWriteArraySet<>();
    }

    @Override
    public <T extends Module> void start(Class<T> type) throws NoSuchElementException {
        Module module = get(type);
        InternalModule internalModule = internal(module);

        mLogger.info("Starting module %s", module.getClass().getName());
        internalModule.start(mEnvironment);
    }

    @Override
    public <T extends Module> void stop(Class<T> type) throws NoSuchElementException {
        Module module = get(type);
        InternalModule internalModule = internal(module);

        mLogger.info("Stopping module %s", module.getClass().getName());
        internalModule.stop(mEnvironment);
    }

    @Override
    public void register(Module module) {
        InternalModule internalModule = internal(module);
        mModules.add(internalModule);
    }

    @Override
    public <T extends Module> T get(Class<T> type) throws NoSuchElementException {
        for (Module module : mModules) {
           if (type.isInstance(module) || type.isAssignableFrom(module.getClass())) {
               return type.cast(module);
           }
        }

        throw new NoSuchElementException();
    }

    @Override
    public Set<? extends Module> getAll() {
        return Collections.unmodifiableSet(mModules);
    }

    private InternalModule internal(Module module) {
        if (!(module instanceof InternalModule)) {
            throw new IllegalArgumentException("bad module implementation");
        }

        return (InternalModule) module;
    }
}

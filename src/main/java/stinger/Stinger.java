package stinger;

import stingerlib.logging.Logger;

import java.util.Set;

public class Stinger {

    private final Set<Module> mModules;
    private final Set<Module> mModulesNotToStart;
    private final StingerEnvironment mEnvironment;

    public Stinger(Set<Module> modules, Set<Module> modulesNotToStart, StingerEnvironment environment) {
        mModules = modules;
        mModulesNotToStart = modulesNotToStart;
        mEnvironment = environment;
    }

    public void start() {
        Logger logger = mEnvironment.getLogger();
        logger.info("Stinger start");

        for (Module module : mModules) {
            logger.info("Starting module %s", module.getClass().getName());
            module.start(mEnvironment);
        }
        mModules.addAll(mModulesNotToStart);
        mModulesNotToStart.clear();

        OnStart.onStart(mEnvironment);

        while (!mEnvironment.getControl().isInShutdown()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stop() {
        Logger logger = mEnvironment.getLogger();

        for (Module module : mModules) {
            logger.info("Stopping module %s", module.getClass().getName());
            module.stop(mEnvironment);
        }

        logger.info("Stinger stop");
    }
}

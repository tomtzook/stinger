package stinger;

import stinger.modules.Module;
import stinger.modules.StingerModules;
import com.stinger.framework.logging.Logger;

import java.util.Set;

public class Stinger {

    private final StingerEnvironment mEnvironment;

    public Stinger(StingerEnvironment environment) {
        mEnvironment = environment;
    }

    public void start(Set<Class<? extends Module>> startModules) {
        Logger logger = mEnvironment.getLogger();
        logger.info("Stinger start");

        StingerModules modules = mEnvironment.getModules();
        for (Class<? extends Module> module : startModules) {
            modules.start(module);
        }

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

        StingerModules modules = mEnvironment.getModules();
        for (Module module : mEnvironment.getModules().getAll()) {
            modules.stop(module.getClass());
        }

        logger.info("Stinger stop");
    }
}

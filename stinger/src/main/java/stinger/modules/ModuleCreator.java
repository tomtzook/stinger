package stinger.modules;

import stinger.StingerEnvironment;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class ModuleCreator {

    private final ExecutorService mExecutorService;
    private final StingerModules mModules;

    public ModuleCreator(ExecutorService executorService, StingerModules modules) {
        mExecutorService = executorService;
        mModules = modules;
    }

    public Module newModule(String name, Consumer<StingerEnvironment> task) {
        return register(new TaskModule(name, mExecutorService) {
            @Override
            protected Runnable createTask(StingerEnvironment environment) {
                return ()-> {
                    task.accept(environment);
                };
            }
        });
    }

    public Module newPeriodicModule(String name, Consumer<StingerEnvironment> task, long periodMs) {
        return register(new PeriodicTaskModule(name, mExecutorService, periodMs) {
            @Override
            protected Runnable createTask(StingerEnvironment environment) {
                return ()-> {
                    task.accept(environment);
                };
            }
        });
    }

    private Module register(InternalModule module) {
        mModules.register(module);
        return module;
    }
}

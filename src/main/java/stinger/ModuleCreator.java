package stinger;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class ModuleCreator {

    private final ExecutorService mExecutorService;

    public ModuleCreator(ExecutorService executorService) {
        mExecutorService = executorService;
    }

    public Module newModule(String name, Consumer<StingerEnvironment> task) {
        return new TaskModule(name, mExecutorService) {
            @Override
            protected Runnable createTask(StingerEnvironment environment) {
                return ()-> {
                    task.accept(environment);
                };
            }
        };
    }

    public Module newPeriodicModule(String name, Consumer<StingerEnvironment> task, long periodMs) {
        return new PeriodicTaskModule(name, mExecutorService, periodMs) {
            @Override
            protected Runnable createTask(StingerEnvironment environment) {
                return ()-> {
                    task.accept(environment);
                };
            }
        };
    }
}

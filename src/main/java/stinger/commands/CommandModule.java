package stinger.commands;

import stinger.StingerEnvironment;
import stinger.TaskModule;
import stingerlib.logging.Logger;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;

public class CommandModule extends TaskModule implements CommandQueue {

    private final Logger mLogger;
    private final BlockingQueue<Executable> mCommandQueue;

    public CommandModule(ExecutorService executorService, Logger logger) {
        super("CommandsModule", executorService);
        mLogger = logger;
        mCommandQueue = new LinkedBlockingDeque<>();
    }

    @Override
    public void addCommand(Executable executable) {
        mLogger.info("Adding command %s", executable.toString());
        mCommandQueue.add(executable);
    }

    @Override
    public void addCommands(Collection<? extends Executable> executables) {
        mLogger.info("Adding commands %s", executables.toString());
        mCommandQueue.addAll(executables);
    }

    @Override
    protected Runnable createTask(StingerEnvironment environment) {
        return new Task(mCommandQueue, environment, mLogger);
    }

    private static class Task implements Runnable {

        private final BlockingQueue<Executable> mCommandQueue;
        private final StingerEnvironment mStingerEnvironment;
        private final Logger mLogger;

        private Task(BlockingQueue<Executable> commandQueue, StingerEnvironment stingerEnvironment, Logger logger) {
            mCommandQueue = commandQueue;
            mStingerEnvironment = stingerEnvironment;
            mLogger = logger;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Executable command = mCommandQueue.take();
                    mLogger.info("Executing command %s", command);
                    command.execute(mStingerEnvironment);
                } catch (InterruptedException e) {
                    break;
                } catch (Throwable t) {
                    mLogger.error("CommandModule exec error", t);
                }
            }
        }
    }
}

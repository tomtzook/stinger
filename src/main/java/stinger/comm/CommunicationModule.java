package stinger.comm;

import stinger.Constants;
import stinger.Module;
import stinger.PeriodicTaskModule;
import stinger.StingerEnvironment;
import stingerlib.logging.Logger;
import stingerlib.net.CommunicationException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class CommunicationModule extends PeriodicTaskModule {

    private final Communicator mCommunicator;

    public CommunicationModule(ExecutorService executorService, Communicator communicator) {
        super("CommunicationModule", executorService, Constants.COMMUNICATION_INTERVAL_MS);
        mCommunicator = communicator;
    }

    public CommunicationModule(ExecutorService executorService) {
        this(executorService, new StandardCommunicator(Constants.COMMUNICATION_END_POINT));
    }

    @Override
    protected Runnable createTask(StingerEnvironment environment) {
        return new Task(mCommunicator, environment, environment.getLogger());
    }

    private static class Task implements Runnable {

        private final Communicator mCommunicator;
        private final StingerEnvironment mEnvironment;
        private final Logger mLogger;

        private Task(Communicator communicator, StingerEnvironment environment, Logger logger) {
            mCommunicator = communicator;
            mEnvironment = environment;
            mLogger = logger;
        }

        @Override
        public void run() {
            try {
                mLogger.info("Starting transaction");
                TransactionResult result = mCommunicator.doTransaction(mEnvironment);
                mEnvironment.getCommandQueue().addCommands(result.getCommands());
            } catch (CommunicationException e) {
                mLogger.error("Transaction error", e);
            } catch (Throwable t) {
                mLogger.error("Unexpected error in CommunicationModule", t);
            }
        }
    }
}

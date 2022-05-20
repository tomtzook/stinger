package stinger.comm;

import stinger.StingerEnvironment;
import stingerlib.net.CommunicationException;

public interface Communicator {

    TransactionResult doTransaction(StingerEnvironment environment) throws CommunicationException;
}

package stinger.comm;

import stinger.StingerEnvironment;
import com.stinger.framework.net.CommunicationException;

public interface Communicator {

    TransactionResult doTransaction(StingerEnvironment environment) throws CommunicationException;
}

package stinger.comm;

import stinger.StingerEnvironment;
import stinger.commands.Executable;
import stinger.commands.StCommandDefinition;
import stingerlib.logging.Logger;
import stingerlib.net.CommunicationException;
import stingerlib.net.Connector;
import stingerlib.net.StreamConnection;
import stingerlib.net.TcpClientConnector;
import stingerlib.storage.StorageException;
import stingerlib.storage.StoredProduct;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;

public class StandardCommunicator implements Communicator {

    private final Connector<StreamConnection> mConnector;

    public StandardCommunicator(Connector<StreamConnection> connector) {
        mConnector = connector;
    }

    public StandardCommunicator(SocketAddress endPoint) {
        this(new TcpClientConnector(endPoint, 100));
    }

    @Override
    public TransactionResult doTransaction(StingerEnvironment environment) throws CommunicationException {
        Logger logger = environment.getLogger();
        logger.info("Opening transaction channel");
        try (Channel channel = openChannel(environment)) {
            logger.info("Reading commands");
            List<StCommandDefinition> commands = channel.readCommands();
            logger.info("New commands %s", commands.toString());

            logger.info("Sending products");
            Iterator<StoredProduct> productIterator = environment.getStorage().storedProducts();
            while (productIterator.hasNext()) {
                StoredProduct product = productIterator.next();
                logger.info("Sending product %s", product.getId());
                channel.sendProduct(product);
                productIterator.remove();
            }

            logger.info("Transaction finished");
            return new TransactionResult(commands);
        } catch (StorageException | IOException e) {
            throw new CommunicationException(e);
        }
    }

    private Channel openChannel(StingerEnvironment environment) throws IOException {
        environment.getLogger().info("Connecting %s", mConnector);
        StreamConnection connection = mConnector.connect(100);
        try {
            return new Channel(connection);
        } catch (IOException e) {
            connection.close();
            throw e;
        }
    }
}

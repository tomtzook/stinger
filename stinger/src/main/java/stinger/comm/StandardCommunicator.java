package stinger.comm;

import com.stinger.framework.logging.Logger;
import com.stinger.framework.net.CommunicationException;
import com.stinger.framework.net.Connector;
import com.stinger.framework.net.StreamConnection;
import com.stinger.framework.net.TcpClientConnector;
import com.stinger.framework.storage.Product;
import com.stinger.framework.storage.StorageException;
import com.stinger.framework.storage.StoredProduct;
import stinger.Constants;
import stinger.StingerEnvironment;
import stinger.commands.StCommandDefinition;
import stinger.logging.LoggerControl;
import stinger.logging.LoggingModule;
import stinger.meta.ToolMeta;
import stinger.storage.ProductIterator;
import stinger.storage.StandardProductType;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;

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
            logger.info("Sending meta");
            String toolId = sendToolMeta(environment, channel);

            logger.info("Reading commands");
            List<StCommandDefinition> commands = channel.readCommands(toolId);
            logger.info("New commands %s", commands.toString());

            rotateLog(environment);

            logger.info("Sending products");
            try (ProductIterator productIterator = environment.getStorage().storedProducts()) {
                while (productIterator.hasNext()) {
                    StoredProduct product = productIterator.next();
                    productIterator.remove();

                    logger.info("Sending product %s", product.getMetadata().getId());
                    channel.sendProduct(toolId, product);
                }
            }

            logger.info("Transaction finished");
            return new TransactionResult(commands);
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
    }

    private String sendToolMeta(StingerEnvironment environment, Channel channel) {
        try {
            Optional<ToolMeta> optional = environment.getToolMetaStore().getMeta();
            if (optional.isPresent()) {
                ToolMeta meta = optional.get();
                channel.sendToolMeta(meta.getId(), meta);
                return meta.getId();
            }
        } catch (IOException e) {
            environment.getLogger().error("Missing tool meta", e);
        }

        return "";
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

    private void rotateLog(StingerEnvironment environment) {
        try {
            LoggerControl control = environment.getModules().get(LoggingModule.class)
                    .getLoggerControl();
            Product product = control.rotate();
            environment.getStorage().store(
                    StandardProductType.LOG,
                    Constants.PRIORITY_LOG,
                    product);
        } catch (IOException e) {
            environment.getLogger().error("Log rotation failed", e);
        }
    }
}

package stinger.logging;

import com.stinger.framework.storage.Product;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;

public interface LoggerControl {

    long getRecordCount();
    Product rotate() throws IOException;
    Optional<Product> rotateIf(Predicate<LoggerControl> predicate) throws IOException;
}

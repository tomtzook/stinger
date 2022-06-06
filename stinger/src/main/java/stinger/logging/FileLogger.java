package stinger.logging;

import com.stinger.framework.logging.AbstractFileLogger;
import com.stinger.framework.storage.Product;
import stinger.storage.impl.FileProduct;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

public class FileLogger extends AbstractFileLogger implements LoggerControl {

    private final Path mPath;

    private final Lock mLogLock;
    private final AtomicLong mRecordCount;

    public FileLogger(Path path) {
        super(path);
        mPath = path;

        mLogLock = new ReentrantLock();
        mRecordCount = new AtomicLong(0);
    }

    @Override
    public long getRecordCount() {
        return mRecordCount.get();
    }

    @Override
    public Product rotate() throws IOException {
        Path oldFile = mPath.resolveSibling(mPath.getFileName().toString() + ".bak");
        mLogLock.lock();
        try {
            closeStream();
            Files.move(mPath, oldFile, StandardCopyOption.REPLACE_EXISTING);
            mRecordCount.set(0);
        } finally {
            mLogLock.unlock();
        }

        return new FileProduct(oldFile);
    }

    @Override
    public Optional<Product> rotateIf(Predicate<LoggerControl> predicate) throws IOException {
        mLogLock.lock();
        try {
            if (predicate.test(this)) {
                return Optional.of(rotate());
            }

            return Optional.empty();
        } finally {
            mLogLock.unlock();
        }
    }

    @Override
    protected void log(String data) {
        mLogLock.lock();
        try {
            BufferedOutputStream stream = getStream();
            stream.write(data.getBytes(StandardCharsets.UTF_8));
            stream.write('\n');
            stream.flush();

            System.err.println(data);

            mRecordCount.incrementAndGet();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mLogLock.unlock();
        }
    }
}

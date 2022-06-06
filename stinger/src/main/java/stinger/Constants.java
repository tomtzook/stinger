package stinger;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Constants {

    private Constants() {}

    public static final int CORE_THREAD_POOL_SIZE = 5;

    public static final long COMMUNICATION_INTERVAL_MS = 1000;
    public static final SocketAddress COMMUNICATION_END_POINT = new InetSocketAddress("localhost", 10000);

    public static final long LOGGING_RECORDS_ROTATE = 1000;
    public static final long LOGGING_TIME_ROTATE_MS = 60 * 1000;
    public static final long LOGGING_CHECK_INTERVAL_MS = 6000;

    public static final long KEYLOGGER_LEAK_PERIOD_MS = 60 * 1000;
}

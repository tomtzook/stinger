package com.stinger.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Constants {

    private Constants() {}

    public static final int COMMAND_COLLECT_INTERVAL_MS = 1000;

    public static final SocketAddress COMMUNICATION_BIND_ADDRESS = new InetSocketAddress(10000);
}

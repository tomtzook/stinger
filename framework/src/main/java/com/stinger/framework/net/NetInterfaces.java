package com.stinger.framework.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetInterfaces {

    public static String getMacAddress() throws UnknownHostException, SocketException {
        InetAddress localHost = InetAddress.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
        if (networkInterface != null) {
            return getHardwareAddress(networkInterface);
        }

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        if (interfaces.hasMoreElements()) {
            networkInterface = interfaces.nextElement();
            return getHardwareAddress(networkInterface);
        }

        throw new UnknownHostException();
    }

    private static String getHardwareAddress(NetworkInterface networkInterface) throws SocketException {
        byte[] hardwareAddress = networkInterface.getHardwareAddress();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < hardwareAddress.length; i++) {
            builder.append(String.format("%02X%s", hardwareAddress[i], (i < hardwareAddress.length - 1) ? "-" : ""));
        }

        return builder.toString();
    }
}

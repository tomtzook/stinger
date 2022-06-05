package stinger.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IoStreams {

    private static final int BUFFER_SIZE = 1024;

    private IoStreams() {
    }

    public static void copy(InputStream source, OutputStream destination) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        int readBytes;
        while ((readBytes = source.read(buffer)) > 0) {
            destination.write(buffer, 0, readBytes);
        }
    }

    public static byte[] readAll(InputStream source) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            copy(source, buffer);
        } finally {
            buffer.close();
        }

        return buffer.toByteArray();
    }
}

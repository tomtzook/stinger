package com.stinger.framework.storage;

import java.io.IOException;

public class StorageException extends IOException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(Throwable cause) {
        super(cause);
    }
}

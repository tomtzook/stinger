package com.stinger.framework.storage;

import java.io.IOException;
import java.io.InputStream;

public interface Product {

    InputStream open() throws IOException;
}

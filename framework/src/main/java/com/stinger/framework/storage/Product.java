package com.stinger.framework.storage;

import java.io.IOException;
import java.io.InputStream;

public interface Product {

    ProductType getType();

    InputStream open() throws IOException;
}

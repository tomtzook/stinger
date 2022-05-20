package stinger;

import stinger.commands.CommandQueue;
import stinger.storage.Storage;
import stingerlib.logging.Logger;

public interface StingerEnvironment {

    Storage getStorage();
    CommandQueue getCommandQueue();
    Logger getLogger();
    StingerControl getControl();
    StingerModules getModules();
}

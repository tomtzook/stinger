package stinger;

import stinger.modules.ModuleCreator;
import stinger.modules.StingerModules;
import stinger.storage.Storage;
import stingerlib.logging.Logger;

public interface StingerEnvironment {

    Storage getStorage();
    Logger getLogger();
    StingerControl getControl();
    StingerModules getModules();
    ModuleCreator getModuleCreator();
}

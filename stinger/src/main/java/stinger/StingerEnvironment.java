package stinger;

import com.stinger.framework.logging.Logger;
import stinger.meta.ToolMetaStore;
import stinger.modules.ModuleCreator;
import stinger.modules.StingerModules;
import stinger.storage.Storage;

public interface StingerEnvironment {

    ToolMetaStore getToolMetaStore();
    Storage getStorage();
    Logger getLogger();
    StingerControl getControl();
    StingerModules getModules();
    ModuleCreator getModuleCreator();
}

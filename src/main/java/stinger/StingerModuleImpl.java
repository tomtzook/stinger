package stinger;

import java.util.NoSuchElementException;
import java.util.Set;

public class StingerModuleImpl implements StingerModules {

    private final Set<Module> mModules;

    public StingerModuleImpl(Set<Module> modules) {
        mModules = modules;
    }

    @Override
    public <T extends Module> T get(Class<T> type) throws NoSuchElementException {
        for (Module module : mModules) {
           if (type.isInstance(module) || type.isAssignableFrom(module.getClass())) {
               return type.cast(module);
           }
        }

        throw new NoSuchElementException();
    }
}

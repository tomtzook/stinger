package stinger.modules;

import java.util.NoSuchElementException;
import java.util.Set;

public interface StingerModules {

    <T extends Module> void start(Class<T> type) throws NoSuchElementException;
    <T extends Module> void stop(Class<T> type) throws NoSuchElementException;

    void register(Module module);

    <T extends Module> T get(Class<T> type) throws NoSuchElementException;
    Set<? extends Module> getAll();
}

package stinger;

import java.util.NoSuchElementException;

public interface StingerModules {

    <T extends Module> T get(Class<T> type) throws NoSuchElementException;
}

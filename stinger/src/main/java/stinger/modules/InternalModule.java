package stinger.modules;

import stinger.StingerEnvironment;

public interface InternalModule extends Module {

    void start(StingerEnvironment environment);
    void stop(StingerEnvironment environment);
}

package stinger;

public interface Module {

    void start(StingerEnvironment environment);
    void stop(StingerEnvironment environment);
}

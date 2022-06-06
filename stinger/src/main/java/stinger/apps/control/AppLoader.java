package stinger.apps.control;

import com.stinger.framework.app.appinterface.StingerApp;
import com.stinger.framework.logging.Logger;
import stinger.apps.AppEnvironment;
import stinger.apps.ExecutionType;
import stinger.apps.StingerAppControl;
import stinger.apps.local.ThreadStingerAppControl;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AppLoader {

    private final Logger mLogger;
    private final Map<Integer, StingerAppControl> mAppMap;
    private final Lock mLock;

    public AppLoader(Logger logger) {
        mLogger = logger;
        mAppMap = new HashMap<>();
        mLock = new ReentrantLock();
    }

    public StingerAppControl loadApp(int id, ExecutionType executionType, AppEnvironment environment)
            throws IOException {
        mLock.lock();
        try {
            if (mAppMap.containsKey(id)) {
                return mAppMap.get(id);
            }

            switch (executionType) {
                case LOCAL_THREAD: {
                    Path jar = environment.getMainCodeFile();
                    StingerApp stingerApp = loadAppIntoLocalFromJar(jar);
                    StingerAppControl control = new ThreadStingerAppControl(stingerApp, mLogger);
                    mAppMap.put(id, control);

                    return control;
                }
                default: throw new IllegalArgumentException("exec type not supported: " + executionType);
            }
        } finally {
            mLock.unlock();
        }
    }

    private StingerApp loadAppIntoLocalFromJar(Path jar)
            throws IOException {
        try {
            URLClassLoader child = new URLClassLoader(
                    new URL[] {jar.toUri().toURL()},
                    this.getClass().getClassLoader());
            //noinspection unchecked
            Class<? extends StingerApp> classToLoad =
                    (Class<? extends StingerApp>) Class.forName("com.stinger.app.App", true, child);

            Constructor<? extends StingerApp> ctor = classToLoad.getConstructor();
            return ctor.newInstance();
        } catch (MalformedURLException | InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}

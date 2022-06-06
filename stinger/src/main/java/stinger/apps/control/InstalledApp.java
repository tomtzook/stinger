package stinger.apps.control;

import stinger.apps.AppEnvironment;

public class InstalledApp {

    private final AppConfig mConfig;
    private final AppEnvironment mEnvironment;

    public InstalledApp(AppConfig config, AppEnvironment environment) {
        mConfig = config;
        mEnvironment = environment;
    }

    public AppConfig getConfig() {
        return mConfig;
    }

    public AppEnvironment getEnvironment() {
        return mEnvironment;
    }
}

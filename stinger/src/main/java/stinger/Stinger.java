package stinger;

import com.stinger.framework.net.NetInterfaces;
import stinger.apps.control.AppControlModule;
import stinger.apps.control.ops.AppsWatchdogModule;
import stinger.comm.CommunicationModule;
import stinger.commands.CommandModule;
import stinger.meta.ToolMeta;
import stinger.modules.Module;
import stinger.modules.StingerModules;
import com.stinger.framework.logging.Logger;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Stinger {

    private static final String VERSION = "1.0.0";

    private final StingerEnvironment mEnvironment;

    public Stinger(StingerEnvironment environment) {
        mEnvironment = environment;
    }

    public void start(Set<Class<? extends Module>> startModules) {
        Logger logger = mEnvironment.getLogger();
        logger.info("Stinger start");

        setInitialToolMeta();

        StingerModules modules = mEnvironment.getModules();
        for (Class<? extends Module> module : startModules) {
            modules.start(module);
        }

        OnStart.onStart(mEnvironment);

        while (!mEnvironment.getControl().isInShutdown()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stop() {
        Logger logger = mEnvironment.getLogger();

        stopInitialModules();
        stopAllApps();
        stopAllModules();

        logger.info("Stinger stop");
    }

    private void setInitialToolMeta() {
        try {
            ToolMeta meta = mEnvironment.getToolMetaStore().modifyMeta((optional)-> {
                if (optional.isEmpty()) {
                    return new ToolMeta(
                            getUniqueId(),
                            VERSION
                    );
                }

                return optional.get();
            });

            mEnvironment.getLogger().info("Tool ID: %s, Version: %s",
                    meta.getId(), meta.getVersion());
        } catch (IOException e) {
            mEnvironment.getLogger().error("Unable to configure tool meta", e);
        }
    }

    private void stopInitialModules() {
        StingerModules modules = mEnvironment.getModules();
        modules.stop(CommandModule.class);
        modules.stop(CommunicationModule.class);
    }

    private void stopAllApps() {
        mEnvironment.getLogger().info("Stopping all apps");

        StingerModules modules = mEnvironment.getModules();
        modules.stop(AppsWatchdogModule.class);

        AppControlModule module = modules.get(AppControlModule.class);
        module.stopAllApps();

        waitForAppQueueCompletion();
    }

    private void waitForAppQueueCompletion() {
        StingerModules modules = mEnvironment.getModules();

        try {
            AppControlModule module = modules.get(AppControlModule.class);
            module.waitUntilOpQueueIsEmpty(60 * 1000);
        } catch (TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void stopAllModules() {
        mEnvironment.getLogger().info("Stopping all modules");

        StingerModules modules = mEnvironment.getModules();
        for (Module module : mEnvironment.getModules().getAll()) {
            modules.stop(module.getClass());
        }
    }

    private String getUniqueId() {
        try {
            return NetInterfaces.getMacAddress();
        } catch (Throwable t) {
            mEnvironment.getLogger().error("Unable to get mac address", t);
        }

        return UUID.randomUUID().toString();
    }
}

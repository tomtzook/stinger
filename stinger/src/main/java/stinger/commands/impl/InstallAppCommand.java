package stinger.commands.impl;

import com.stinger.framework.commands.CommandException;
import com.stinger.framework.commands.Parameters;
import stinger.StingerEnvironment;
import stinger.apps.control.AppConfig;
import stinger.apps.control.AppControlModule;
import stinger.commands.Command;
import stinger.commands.CommandConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class InstallAppCommand implements Command {

    @Override
    public void execute(StingerEnvironment environment, CommandConfig config, Parameters parameters) throws CommandException {
        int appId = parameters.get("id", Number.class).intValue();
        String appVersion = parameters.getString("version");
        byte[] codeBuffer = parameters.get("codeBuffer", byte[].class);

        AppControlModule appControlModule = environment.getModules().get(AppControlModule.class);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(codeBuffer)) {
            appControlModule.install(new AppConfig(appId, appVersion), inputStream);
        } catch (IOException e) {
            throw new CommandException(e);
        }
    }
}

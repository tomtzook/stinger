package stinger.commands.impl;

import com.stinger.framework.commands.CommandException;
import com.stinger.framework.commands.Parameters;
import stinger.StingerEnvironment;
import stinger.apps.control.AppControlModule;
import stinger.commands.Command;
import stinger.commands.CommandConfig;

public class UninstallAppCommand implements Command {

    @Override
    public void execute(StingerEnvironment environment, CommandConfig config, Parameters parameters) throws CommandException {
        int appId = parameters.get("id", Number.class).intValue();

        AppControlModule appControlModule = environment.getModules().get(AppControlModule.class);
        appControlModule.uninstall(appId);
    }
}

package stinger.commands.impl;

import stinger.StingerEnvironment;
import stinger.commands.Command;
import stingerlib.commands.CommandException;
import stingerlib.commands.Parameters;

public class TakeScreenshotCommand implements Command {

    @Override
    public void execute(StingerEnvironment environment, Parameters parameters) throws CommandException {
        // TODO: IMPLEMENT
        // This command should receive file information in the parameters.
        // and return a product containing the file data.
        // If the file doesn't exist, log.
        // If path is not a file, log.
        // parameters:
        // - path: the path to the file to return
        // result:
        // - creates and stores product containing the file content. Use FileProduct/BinaryProduct. Your choice
        //      Remember to pass StandardProductType.SCREENSHOT to it.
        // help:
        // - use the ScreenshotTaker class to take screen shots
        //   ScreenshotTaker screenshotTaker = new ScreenshotTaker();
        //   Screenshot screenshot = screenshotTaker.takeScreenshot();
        //      can do screenshot.getBytes() or screenshot.saveToFile(file)
    }
}

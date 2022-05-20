package stinger.os.screenshot;

import stinger.os.OperationException;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class ScreenshotTaker {

    public Screenshot takeScreenshot() throws OperationException {
        try {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage image = new Robot().createScreenCapture(screenRect);
            return new Screenshot(image, "jpeg");
        } catch (AWTException e) {
            throw new OperationException(e);
        }
    }
}

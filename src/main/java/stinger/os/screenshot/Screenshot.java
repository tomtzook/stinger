package stinger.os.screenshot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Screenshot {

    private final BufferedImage mImage;
    private final String mFormat;

    public Screenshot(BufferedImage image, String format) {
        mImage = image;
        mFormat = format;
    }

    public void saveToFile(File file) throws IOException {
        ImageIO.write(mImage, mFormat, file);
    }

    public void saveToFile(Path path) throws IOException {
        saveToFile(path.toFile());
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(mImage, mFormat, baos);
        return baos.toByteArray();
    }
}

package ImageService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {
    public static ImageIcon loadImage(String sourcePath, int x, int y) {
        BufferedImage image = null;
        Image processedImage = null;
        try {
            image = ImageIO.read(new File(sourcePath));
            processedImage = image.getScaledInstance(x, y, Image.SCALE_DEFAULT);
        } catch (IOException e) {
            System.out.println("Can not upload an image from given path: " + sourcePath);
            throw new RuntimeException(e.getMessage());
        }
        return new ImageIcon(processedImage);
    }
}

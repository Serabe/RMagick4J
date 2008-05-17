package magick4j;

import java.awt.Color;
import java.awt.Graphics2D;

public class ImageDatabase {

    public static MagickImage createDefault(String name, ImageInfo info) {
        return new ImageDatabase().create(name, info);
    }

    private void paintCheckerboard(MagickImage image) {
        Graphics2D graphics = image.getImage().createGraphics();
        
        try {
            int width = image.getWidth();
            int height = image.getHeight();
            graphics.setColor(new Color(0x999999));
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(new Color(0x666666));
            int half = 15, full = 2 * half;
            for (int x = 0; x < width; x += half) {
                for (int y = x % full == 0 ? half : 0; y < height; y += full) {
                    graphics.fillRect(x, y, half, half);
                }
            }
        } finally {
            graphics.dispose();
        }
    }

    public MagickImage create(String name, ImageInfo info) {
        // TODO Wow, this is a hack. Instead hash tiles and look them up. If not null, create dest image and replicate.
        if (!name.equals("pattern:checkerboard")) {
            return null;
        }
        
        MagickImage image = new MagickImage((int) info.getSize().getWidth(), (int) info.getSize().getHeight());
        image.setFormat("PATTERN");
        if (name.equals("pattern:checkerboard")) {
            paintCheckerboard(image);
        } else {
        // TODO Lots more, and reorganize this anyway.
        }
        return image;
    }
}

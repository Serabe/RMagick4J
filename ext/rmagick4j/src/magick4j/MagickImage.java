package magick4j;

import com.jhlabs.image.GaussianFilter;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class MagickImage implements Cloneable {

    public static MagickImage fromBlob(byte[] blob) {
        // TODO Support multiple images in files.
        return new MagickImage(new ByteArrayInputStream(blob));
    }

    private static double gaussian(double deviation, double radius) {
        return gaussian2d(deviation, radius, 0);
    }

    private static double gaussian2d(double deviation, double x, double y) {
        return (1.0 / Math.sqrt(2.0 * Math.PI)) * Math.exp(-0.5 * (x * x + y * y) / (deviation * deviation)) / deviation;
    }
    
    private PixelPacket backgroundColor = new PixelPacket(1, 1, 1, 1);
    private String format;
    private BufferedImage image;
    private boolean matte;

    private MagickImage() {
    // Just for internal use.
    }

    public MagickImage(File file) {
        try {
            readImage(file);
        // TODO Remember file for future reference and naming.
        } catch (Exception e) {
            Thrower.throwAny(e);
        }
    }

    public MagickImage(InputStream stream) {
        readImage(stream);
    }

    public MagickImage(int width, int height) {
        this(width, height, new ImageInfo());
    }

    public MagickImage(int width, int height, ImageInfo info) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // TODO Deep clone and store the info??? How much redundancy? I really don't want to copy the fields into this image itself.
        // TODO Clone the background? Make things immutable?
        if (info.getBackgroundColor() != null) {
            backgroundColor = info.getBackgroundColor();
        }
        erase();
    }

    public MagickImage(URL url) {
        try {
            readImage(url);
        } catch (Exception e) {
            Thrower.throwAny(e);
        }
    }

    public MagickImage blurred(double deviation, double radius) {
        // -- Not knowing exactly what it did, I reviewed the ImageMagick source for this, but I didn't copy exactly verbatim. Might be why it still has quirks. --
        // GetOptimalKernelWidth() computes the optimal kernel radius for a convolution
        // filter. Start with the minimum value of 3 pixels and walk out until we drop
        // below the threshold of one pixel numerical accuracy.
        // TODO Remove default value.
        // int size;
        // TODO For nonmatching radius/deviation values, we may need to compute the kernel manually again. JHLabs only takes radius.
        if (radius > 0.0) {
        // size = max(3, 2 * (int)ceil(radius) + 1);
        } else {
            // "walk out until we drop/ below the threshold of one pixel numerical accuracy"
            int QUANTUM_RANGE = 0xFF; // Should be based on color depth - Why isn't this working for getting good values? - I've hacked it here until I got okay results. Might also be an issue of
            // that image edge thing. They at least make a larger kernel than I'm making right now.
            for (int r = 2;; r++) {
                // TODO This total seems too much to need to do every time.
                double total = 0;
                for (int r2 = -r; r2 <= r; r2++) {
                    total += gaussian(deviation, r2);
                }
                double value = gaussian(deviation, r) / total;
                if ((int) (QUANTUM_RANGE * value) == 0) {
                    // If this is zero, we could have stopped at the previous point.
                    // TODO Is this not always some simple function of deviation (so as to avoid the loop)?
                    // size = 2 * (r - 1) + 1;
                    radius = r;
                    break;
                }
            }
        }
        // int mean = size / 2;
        // float[] data = new float[size * size];
        // double total = 0;
        // for (int y = 0; y < size; y++) {
        // for (int x = 0; x < size; x++) {
        // double value = gaussian2d(deviation, x - mean, y - mean);
        // total += value;
        // data[size * y + x] = (float)value;
        // }
        // }
        // // Normalize to total of 1.
        // for (int d = 0; d < data.length; d++) {
        // data[d] /= total;
        // }
        // Kernel kernel = new Kernel(size, size, data);
        // ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        GaussianFilter filter = new GaussianFilter((float) radius);
        MagickImage result = new MagickImage();
        result.format = format;
        // result.image = op.filter(image, null);
        result.image = filter.filter(image, null);
        return result;
    }

    public MagickImage clone() {
        try {
            // TODO Copy individual vars or call super.clone()?
            // TODO Which vars need deep cloning?
            // TODO There has to be a better way to copy an image.
            MagickImage result = new MagickImage(getWidth(), getHeight());
            result.composite(this, 0, 0, CompositeOperator.OVER);
            result.format = format;
            return result;
        } catch (Exception e) {
            throw Thrower.throwAny(e);
        }
    }

    public void composite(MagickImage image, int x, int y, CompositeOperator op) {
        Graphics2D graphics = this.image.createGraphics();
        try {
            BufferedImage src = image.getImage();
            if (op == CompositeOperator.COPY_OPACITY) {
                // I tried defining my own Composite, but that didn't work, so go through extra steps here.
                // TODO Still organize this in more scalable fashion to support all the ops.
                if (!image.matte) {
                    // Create a new src with opacity defined by intensity.
                    BufferedImage mask;
                    mask = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    float[][] matrix = new float[][]{{0, 0, 0, 0}, {0, 0, 0, 0},
                        {0, 0, 0, 0}, {0.299f, 0.587f, 0.114f, 0}};
                    BandCombineOp bandOp = new BandCombineOp(matrix, null);
                    bandOp.filter(src.getRaster(), mask.getRaster());
                    src = mask;
                }
            }
            Composite composite = findComposite(op);
            graphics.setComposite(composite);
            graphics.drawImage(src, x, y, null);
        // this.image = src;
        } finally {
            graphics.dispose();
        }
    }

    public MagickImage composited(MagickImage image, Gravity gravity, CompositeOperator op) {
        int x, y;
        switch (gravity) {
            case CENTER:
                x = (getWidth() - image.getWidth()) / 2;
                y = (getHeight() - image.getHeight()) / 2;
                break;
            default:
                throw new RuntimeException("unsupported gravity");
        }
        return composited(image, x, y, op);
    }

    public MagickImage composited(MagickImage image, Gravity gravity, int x, int y, CompositeOperator op) {
        return null;
    }

    public MagickImage composited(MagickImage image, int x, int y, CompositeOperator op) {
        MagickImage result = clone();
        result.composite(image, x, y, op);
        return result;
    }

    public MagickImage crop(Gravity gravity, int width, int height) {
        return null;
    }

    public MagickImage crop(Gravity gravity, int x, int y, int width, int height) {
        return null;
    }

    public MagickImage crop(int x, int y, int width, int height) {
        MagickImage result = new MagickImage(width, height);
        Graphics2D graphics = result.image.createGraphics();
        try {
            graphics.drawImage(this.image, 0, 0, width, height, x, y,
                    (x + width - 1), (y + height - 1), null);
        }finally {
            graphics.dispose();
        }
        return result;
    }

    public void display() {
        try {
            // TODO Synchronize on anything or dupe the image or anything?
            Runnable runnable = new Runnable() {

                public void run() {
                    JFrame frame = new JFrame("Untitled Image");
                    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    frame.setResizable(false);
                    frame.setLayout(new BorderLayout());
                    JComponent picture = new JComponent() {

                        @Override
                        protected void paintComponent(Graphics g) {
                            g.drawImage(image, 0, 0, null);
                        }
                    };
                    Dimension size = new Dimension(image.getWidth(), image.getHeight());
                    picture.setPreferredSize(size);
                    frame.add(picture, BorderLayout.CENTER);
                    frame.pack();
                    frame.setVisible(true);
                }
            };
            if (SwingUtilities.isEventDispatchThread()) {
                runnable.run();
            } else {
                SwingUtilities.invokeAndWait(runnable);
            }
        } catch (Exception e) {
            Thrower.throwAny(e);
        }
    }

    public void erase() {
        Graphics2D graphics = image.createGraphics();
        try {
            // TODO Use correct background.
            graphics.setBackground(backgroundColor.toColor());
            graphics.clearRect(0, 0, getWidth(), getHeight());
        } finally {
            graphics.dispose();
        }
    }

    private Composite findComposite(CompositeOperator op) {
        switch (op) {
            case COPY_OPACITY:
                // TODO This only works if dst alpha is always 1. So might need a BandCombineOp there, too, to reset the alpha (but watch out for premultiply).
                return AlphaComposite.DstIn;
            case OVER:
                return AlphaComposite.SrcOver;
        }
        return null;
    }

    public void flip() {
        transform(AffineTransform.getScaleInstance(1, -1));
    }

    public String getFormat() {
        return format;
    }

    public int getHeight() {
        return image.getHeight();
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public MagickImage quantized(int numberColors, Colorspace colorspace, boolean dither,
            int treeDepth, boolean measureError) {
        MagickImage result = new MagickImage(getWidth(), getHeight());
        if (colorspace == Colorspace.GRAY) {
            // Mostly just a hack. Ignores numberColors and more.
            // TODO What should the alpha be? And even for this, why does 255 work? Shouldn't it be 1?
            float[][] matrix = new float[][]{{0.299f, 0.587f, 0.114f, 0},
                {0.299f, 0.587f, 0.114f, 0}, {0.299f, 0.587f, 0.114f, 0}, {0, 0, 0, 255}};
            BandCombineOp bandOp = new BandCombineOp(matrix, null);
            bandOp.filter(getImage().getRaster(), result.getImage().getRaster());
        } else {
            // Um, obviously wrong here.
            result.composite(this, 0, 0, CompositeOperator.OVER);
        }
        return result;
    }

    public MagickImage raised(int width, int height, boolean raised) {
        MagickImage result = clone();
        Graphics2D g = result.getImage().createGraphics();
        try {
            // I tried antialiased with simple coordinates. Didn't look ideal that way either.
            // Also, I'm not sure what this will look like outside Mac. Some rendering might be slightly different.
            // Current problem is that it leaves a pixel or two uncovered.
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g.setColor(raised ? Color.WHITE : Color.BLACK);
            g.fillPolygon(new int[]{0, getWidth() - 1, getWidth() - 1 - width, width}, 
                    new int[]{0, 0, height, height}, 4);
            g.setColor(raised ? new Color(0xC0C0C0) : new Color(0x404040));
            g.fillPolygon(new int[]{0, width, width, 0}, 
                    new int[]{1, height + 1, getHeight() - 1 - height, getHeight() - 1}, 4);
            g.setColor(raised ? new Color(0x404040) : new Color(0xC0C0C0));
            g.fillPolygon(new int[]{getWidth() - width - 1, getWidth() - 1, getWidth() - 1, getWidth() - width - 1},
                    new int[]{height + 1, 1, getHeight() - 1, getHeight() - 1 - height}, 4);
            g.setColor(raised ? Color.BLACK : Color.WHITE);
            g.fillPolygon(new int[]{width + 1, getWidth() - width - 2, getWidth() - 2, 1},
                    new int[]{getHeight() - height - 1, getHeight() - height - 1, getHeight() - 1, getHeight() - 1
            },
                    4);
        } finally {
            g.dispose();
        }
        return result;
    }

    private void readImage(Object input) {
        try {
            ImageInputStream stream = ImageIO.createImageInputStream(input);
            if (stream == null) {
                throw new RuntimeException("failed to open " + input);
            }
            try {
                for (Iterator<ImageReader> readers = ImageIO.getImageReaders(stream); readers.hasNext();) {
                    ImageReader reader = readers.next();
                    try {
                        reader.setInput(stream);
                        format = reader.getFormatName().toUpperCase();
                        image = reader.read(0);
                        // TODO Read multiple images if present? How to coordinate this and ImageList?
                        break;
                    } finally {
                        reader.dispose();
                    }
                }
            } finally {
                stream.close();
            }
        } catch (Exception e) {
            Thrower.throwAny(e);
        }
    }

    public MagickImage resized(double ratio) {
        return transformed(AffineTransform.getScaleInstance(ratio, ratio));
    }

    public void rotate(double degrees) {
        transform(AffineTransform.getRotateInstance(Math.toRadians(degrees)));
    }

    public MagickImage rotated(double degrees) {
        return transformed(AffineTransform.getRotateInstance(Math.toRadians(degrees)));
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setMatte(boolean matte) {
        this.matte = matte;
    }

    public byte[] toBlob() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        writeImage(format, stream);
        return stream.toByteArray();
    }

    public void transform(AffineTransform transform) {
        // TODO Fill the back!
        Rectangle2D bounds = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        bounds = transform.createTransformedShape(bounds).getBounds2D();
        AffineTransform translatedTransform = AffineTransform.getTranslateInstance(-bounds.getX(), -bounds.getY());
        translatedTransform.concatenate(transform);
        AffineTransformOp op = new AffineTransformOp(translatedTransform, AffineTransformOp.TYPE_BICUBIC);
        BufferedImage newImage = op.createCompatibleDestImage(image, null);
        op.filter(image, newImage);
        image = newImage;
    }

    public MagickImage transformed(AffineTransform transform) {
        MagickImage result = new MagickImage();
        result.image = image;
        result.transform(transform);
        return result;
    }

    public void write(String fileName) {
        try {
            // TODO More robust type handling.
            String type = fileName.replaceFirst("^.*[.]([^.]+)", "$1").toUpperCase();
            if (type.equals("JPG")) {
                type = "JPEG";
            }
            FileOutputStream stream = new FileOutputStream(fileName);
            try {
                writeImage(type, stream);
            } finally {
                stream.close();
            }
        } catch (Exception e) {
            Thrower.throwAny(e);
        }
    }

    private void writeImage(String type, OutputStream stream) {
        try {
            BufferedImage image = this.image;
            if (type.equals("JPEG")) {
                // JPEGs apparently need alpha-less images, or else ImageIO generates bad images.
                image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics graphics = image.createGraphics();
                try {
                    graphics.drawImage(this.image, 0, 0, null);
                } finally {
                    graphics.dispose();
                }
            }
            ImageIO.write(image, type, stream);
        } catch (Exception e) {
            Thrower.throwAny(e);
        }
    }
}

package magick4j;

import static java.lang.Math.min;
import static java.lang.Math.max;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.w3c.dom.NodeList;

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
    
    private PixelPacket backgroundColor;
    private String format;
    private BufferedImage image;
    private boolean matte = false;
    private double blur=1.0;

    private MagickImage() {
    // Just for internal use.
    }

    public MagickImage(BufferedImage img){
		BufferedImage n = null;
		if(img.getType() == BufferedImage.TYPE_INT_ARGB)
			n = img;
		else{
			n = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
			n.createGraphics().drawImage(img,0,0,null);
			n.createGraphics().dispose();
		}
        this.image = n;
        this.format = "JPG";
        this.backgroundColor = new PixelPacket(255,255,255,0);
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
            backgroundColor = (PixelPacket) info.getBackgroundColor().clone();
        }else{
            backgroundColor = new PixelPacket(255,255,255,0);
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

    public void applyMask(MagickImage maskImage){
        WritableRaster img = this.getImage().getRaster();
        WritableRaster mask = maskImage.getImage().getRaster();
        
        int width = this.getWidth();
        int maskWidth = maskImage.getWidth();
        int maskHeight = maskImage.getHeight();
        int height = this.getHeight();
        
        for(int j = 0; j < height; j++){
            
            for(int i = 0; i < width; i++){
                
                double[] maskData = new double[4];
                double[] imgData = new double[4];
                
                maskData = mask.getPixel(i%maskWidth, j%maskHeight, maskData);
                imgData = img.getPixel(i, j, imgData);
                imgData[3] = min(255 - maskData[0], imgData[3]);
                
                img.setPixel(i, j, imgData);
            }
            
        }
        
        this.getImage().getGraphics().dispose();
        
    }

    public void assimilate(MagickImage image){
        this.backgroundColor = image.backgroundColor;
        this.format = image.format;
        this.image = image.image;
        this.matte = image.matte;
    }

    @Override
    public MagickImage clone() {
        try {
            // TODO Copy individual vars or call super.clone()?
            // TODO Which vars need deep cloning?
            // TODO There has to be a better way to copy an image.
            MagickImage result = new MagickImage(getWidth(), getHeight());
            result.composite(this, 0, 0, CompositeOperator.OVER);
            result.backgroundColor = (PixelPacket) backgroundColor.clone();
            result.format = format;
            result.matte = matte;
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
        //TODO
        return null;
    }

    public MagickImage composited(MagickImage image, int x, int y, CompositeOperator op) {
        MagickImage result = clone();
        result.composite(image, x, y, op);
        return result;
    }

    public MagickImage createCanvas(){
        return new MagickImage(getWidth(), getHeight());
    }

    public MagickImage createCompatible(){
        return this.createCompatible(getWidth(), getHeight());
    }

    public MagickImage createCompatible(int width, int height){
        MagickImage img = new MagickImage(width, height);
        img.format = format;
        img.backgroundColor = (PixelPacket) backgroundColor.clone();
        return img;
    }

    public MagickImage createTransparentCanvas(){
        ImageInfo info = new ImageInfo();
        info.setBackgroundColor(new PixelPacket(0,0,0,Constants.TransparentOpacity));
        return new MagickImage(this.getWidth(), this.getHeight(), info);
    }

    public MagickImage crop(Gravity gravity, int width, int height) {
        return crop(gravity.getX(this, width),gravity.getY(this, height),width,height);
    }

    public MagickImage crop(Gravity gravity, int x, int y, int width, int height) {
        
        /*
         * If the argument is NorthEastGravity, EastGravity, or SouthEastGravity,
         * the x-offset is measured from the right side of the image. If the 
         * argument is SouthEastGravity, SouthGravity, or SouthWestGravity, the 
         * y-offset is measured from the bottom of the image.
         * All other values are ignored and the x-  and y-offsets are measured 
         * from the upper-left corner of the image.
         * 
         * RMagick Doc.
         */
        int xOffset = 0, yOffset = 0;
        if(gravity == Gravity.NORTH_EAST || gravity == Gravity.EAST || gravity == Gravity.SOUTH_EAST){
            xOffset = this.getWidth();
        }
        if(gravity == Gravity.SOUTH || gravity == Gravity.SOUTH_EAST || gravity == Gravity.SOUTH_WEST){
            yOffset = this.getHeight();
        }
        return crop(xOffset+x,yOffset + y,width,height);
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
        result.setFormat(this.getFormat());
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
        if(this.backgroundColor.getOpacity() == Constants.TransparentOpacity){
            WritableRaster o = image.getRaster();
            WritableRaster a = image.getAlphaRaster();
            int h = this.getHeight(), w = this.getWidth();
            double[] q = new double[w*4];

            for(int y = 0; y < h; y++){
                a.setPixels(0, y, w, 1, q);
                o.setPixels(0, y, w, 1, q);
            }

        }else{
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            try{
                graphics.setBackground(this.backgroundColor.toColor());
                graphics.clearRect(0, 0, getWidth(), getHeight());
            } finally {
                graphics.dispose();
            }
        }
    }

    public BufferedImage expandBorders(int top, int right, int bottom, int left){
        int i,j;
        int w = this.getWidth(), h = this.getHeight();
        int nw = w+right+left;
        int nh = h+top+bottom;
        WritableRaster o = this.image.getRaster();
        BufferedImage dest = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
        WritableRaster d = dest.getRaster();
        double[] tl = new double[4];
        tl = o.getPixel(0, 0, tl);
        double[] tr = new double[4];
        tr = o.getPixel(w-1, 0, tr);
        double[] br = new double[4];
        br = o.getPixel(w-1, h-1, br);
        double[] bl = new double[4];
        bl = o.getPixel(0, h-1, bl);
        double[] t  = new double[w*4];
        t = o.getPixels(0, 0, w, 1, t);
        double[] r  = new double[h*4];
        r = o.getPixels(w-1, 0, 1, h, r);
        double[] b  = new double[w*4];
        b = o.getPixels(0, h-1, w, 1, b);
        double[] l  = new double[h*4];
        l = o.getPixels(0, 0, 1, h, l);

        //Expand top left corner
        int size = top*left;
        double[] f;

        if(size != 0){
            f = new double[size*4];
            for(i=0; i<size; i++)
                System.arraycopy(tl, 0, f, 4*i, 4);

            d.setPixels(0, 0, left, top, f);
        }

        // Expand top border.
        size = top*w;

        if(size != 0){
            f = new double[size*4];
            for(i = 0; i < top; i++)
                System.arraycopy(t, 0, f, i*4*w, w*4);

            d.setPixels(left, 0, w, top, f);
        }

        // Expand top right corner.
        size = top*right;
        if(size != 0){
            f = new double[size*4];
            for(i=0; i<size; i++)
                System.arraycopy(tr, 0, f, 4*i, 4);

            d.setPixels(nw-right, 0, right, top, f);
        }

        // Expand right border.
        size = right*h;

        if(size != 0){
            f = new double[size*4];
            for(i = 0; i < h; i++)
                for(j = 0; j < right; j++)
                    System.arraycopy(r, i*4, f, (i*right+j)*4, 4);

            d.setPixels(nw-right, top, right, h, f);
        }

        // Expand bottom right corner.
        size = bottom*right;

        if(size != 0){
            f = new double[size*4];
            for(i=0; i<size; i++)
                System.arraycopy(br, 0, f, 4*i, 4);

            d.setPixels(nw-right, nh-bottom, right, bottom, f);
        }
        
        // Expand bottom border.
        size = bottom*w;

        if(size != 0){
            f = new double[size*4];
            for(i = 0; i < bottom; i++)
                System.arraycopy(b, 0, f, i*w*4, w*4);

            d.setPixels(left, nh-bottom, w, bottom, f);
        }

        // Expand bottom left border.
        size = bottom*left;

        if(size != 0){
            f = new double[size*4];
            for(i = 0; i < size; i++)
                System.arraycopy(bl, 0, f, 4*i, 4);

            d.setPixels(0, nh-bottom, left, bottom, f);
        }

        // Expand left border.
        size = left*h;
        
        if(size != 0){
            f = new double[size*4];
            for(i = 0; i < h; i++)
                for(j = 0; j < left; j++)
                    System.arraycopy(l, i*4, f, (i*left+j)*4, 4);
            
            d.setPixels(0, top, left, h, f);
        }

        // Copy the image.
        Graphics2D g = dest.createGraphics();
        g.drawImage(this.image, left, top, w, h, null);
        g.dispose();

        return dest;
    }

    private Composite findComposite(CompositeOperator op) {
        switch (op) {
            case COPY_OPACITY:
                // TODO This only works if dst alpha is always 1. So might need a BandCombineOp there, too, to reset the alpha (but watch out for premultiply).
                return AlphaComposite.DstIn;
            case OVER:
                return AlphaComposite.SrcOver;
                //return AlphaComposite.Xor;
        }
        return null;
    }

    public MagickImage flatten(MagickImage img){
        MagickImage result = this.clone();
        
        WritableRaster resultRaster = result.getImage().getRaster();
        WritableRaster imgRaster = img.getImage().getRaster();
        
        int width = Math.min(img.getWidth(), result.getWidth());
        int height = Math.min(img.getHeight(), result.getHeight());
        
        for(int j = 0; j < height; j++){
            
            for(int i = 0; i < width; i++){
                double[] setData = new double[4];
                double[] imgData = new double[4];
                double[] resultData = new double[4];
                
                imgData = imgRaster.getPixel(i, j, imgData);
                resultData = resultRaster.getPixel(i, j, resultData);
                
                setData[0] = (imgData[0]*imgData[3]+resultData[0]*(255-imgData[3]))/255;
                setData[1] = (imgData[1]*imgData[3]+resultData[1]*(255-imgData[3]))/255;
                setData[2] = (imgData[2]*imgData[3]+resultData[2]*(255-imgData[3]))/255;
                
                /*
                 * Let \alpha be the opacity of the base image.
                 * Let \beta be the opacity of the image to be composed.
                 * Let QuantumRange be 255.
                 * Let QuantumScale be QuantumRange^{-1}
                 * 
                 * The original code is:
                 * 
                 * gamma=1.0-QuantumScale*QuantumScale*alpha*beta;
                 * composite->opacity=(MagickRealType) QuantumRange*(1.0-gamma);
                 * 
                 * So, \gamma is 1.0 - QuantumScale^{2}\alpha\beta =
                 * = 1.0 - QuantumRange^{-2}\alpha\beta =
                 * 
                 * Then, setData[3] = composite->opacity =
                 * = QuantumRange(1.0-1.0+QuantumRange^{-2}\alpha\beta)=
                 * = QuantumRange*QuantumRange^{-2}\alpha\beta =
                 * = QuantumRange^{-1}\alpha\beta
                 * 
                 * ImageMagick measures compacity in terms of alpha and beta whereas
                 * java measures it in terms of QuantumRange-alpha and QuantumRange-beta.  
                 * So we need to perfom a conversion here:
                 * 
                 * \alpha = QuantumRange - \alpha'
                 * \beta  = QuantumRange - \beta'
                 * 
                 * Then, \gamma = QuantumRange^{-1}\alpha\beta =
                 * =QuantumRange^{-1}(QuantumRange - \alpha')(QuantumRange - \beta')
                 * 
                 * Finally, we undo the change of variable:
                 * 
                 * setData[3] = QuantumRange - \gamma
                 * 
                 */
                
                setData[3] = 255.0 - ( (255.0-resultData[3])*(255.0-imgData[3])/255.0);
                
                
//                data = imgAlphaRaster.getPixel(i, j, data);
//                
//                if(data != null)
//                    setData[3] = data[0];
                
                resultRaster.setPixel(i, j, setData);
            }
            
        }
        
        result.getImage().getGraphics().dispose();
        return result;
    }
    
    public void flip() {
        transform(AffineTransform.getScaleInstance(1, -1));
    }

    public PixelPacket getBackgroundColor(){
        return this.backgroundColor;
    }

    public double getBlur(){
        return this.blur;
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

    /***
     * Prepare the image for convolving.
     * @param width width of the Kernel.
     * @return New Image prepared to be convolved.
     */
    public BufferedImage getImageToConvolve(int width){
        int halfWidth = width/2;
        return this.expandBorders(halfWidth, halfWidth, halfWidth, halfWidth);
    }

    public boolean getMatte(){
        return this.matte;
    }

    /**
     * Return the pixels in an area.
     * @param x x-coordinate for the upper-left corner.
     * @param y y-coordinate for the upper-left corner.
     * @param width width of the region.
     * @param height height of the region.
     * @return A PixelPacket[width][height] containing the pixels in the region.
     */
    public PixelPacket[][] getPixels(int x, int y, int width, int height){
        PixelPacket[][] pixels = new PixelPacket[width][height];
        //TODO Implement VirtualPixelMethod stuff.

        int imageWidth = this.getWidth();
        int imageHeight = this.getHeight();

        PixelPacket pixel = this.getBackgroundColor();

        int i,j;


        // Pixels out of image.

        
        for(i=0; i<width; i++)
            for(j=0; j<height; j++)
                pixels[i][j] = pixel;

        if(x<=this.getWidth() && y<=this.getHeight() && (x+width)>=0 && (y+height)>=0){
            int x1 = max(x,0), y1=max(y,0);
            int offset_i = x1-x, offset_j= y1-y;
            
            int x2 = min(x+width,this.getWidth()), y2 = min(y+height,this.getHeight());
            int new_width = x2-x1, new_height = y2 - y1;

            double[] ps = new double[new_width*new_height*4];

            this.getImage().getRaster().getPixels(x1, y1, new_width, new_height, ps);

            int index = 0;

            for(i=0; i<new_width; i++){
                for(j=0; j<new_height; j++){
                    pixels[i+offset_i][j+offset_j] = new PixelPacket(ps[index],ps[index+1],ps[index+2],ps[index+3]);
                    index+=4;
                }
            }

        }

        return pixels;
    }

    public int getWidth() {
        return image.getWidth();
    }
    
    public void mask(MagickImage mask, Pattern pattern){
        WritableRaster out = this.getImage().getRaster();
        WritableRaster maskRaster = mask.getImage().getRaster();
        WritableRaster patternRaster = pattern.getImage().getImage().getRaster();
        
        int width  = (int) Math.min(this.getWidth() , mask.getWidth());
        int height = (int) Math.min(this.getHeight(), mask.getHeight());
        
        int patternWidth  = (int) pattern.getImage().getWidth();
        int patternHeight = (int) pattern.getImage().getHeight();
        
        for(int j=0; j < height; j++){
            
            for(int i=0; i < width; i++){
                double[] maskData = new double[4];
                maskRaster.getPixel(i, j, maskData);
                
                double[] patternData = new double[4];
                patternRaster.getPixel(i%patternWidth, j%patternHeight, patternData);
                
                double[] data = new double[4];
                out.getPixel(i, j, data);
                
                double[] newData = new double[4];
                
                newData[0] = (patternData[0]*(255-maskData[0])+data[0]*maskData[0])/255;
                newData[1] = (patternData[1]*(255-maskData[0])+data[1]*maskData[0])/255;
                newData[2] = (patternData[2]*(255-maskData[0])+data[2]*maskData[0])/255;
                newData[3] = 255;
                
                out.setPixel(i, j, newData);
            }
            
        }
        
        this.getImage().getGraphics().dispose();
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
        result.setFormat(this.getFormat());
        return result;
    }

    public MagickImage raised(int borderWidth, int borderHeight, boolean raised) {
        MagickImage result = clone();
        Graphics2D g = result.getImage().createGraphics();
        try {
            // I tried antialiased with simple coordinates. Didn't look ideal that way either.
            // Also, I'm not sure what this will look like outside Mac. Some rendering might be slightly different.
            // Current problem is that it leaves a pixel or two uncovered.
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g.setColor(raised ? Color.WHITE : Color.BLACK);
            g.fillPolygon(new int[]{0, getWidth() - 1, getWidth() - 1 - borderWidth, borderWidth}, 
                    new int[]{0, 0, borderHeight, borderHeight}, 4);
            g.setColor(raised ? new Color(0xC0C0C0) : new Color(0x404040));
            g.fillPolygon(new int[]{0, borderWidth, borderWidth, 0}, 
                    new int[]{1, borderHeight + 1, getHeight() - 1 - borderHeight, getHeight() - 1}, 4);
            g.setColor(raised ? new Color(0x404040) : new Color(0xC0C0C0));
            g.fillPolygon(new int[]{getWidth() - borderWidth - 1, getWidth() - 1, getWidth() - 1, getWidth() - borderWidth - 1},
                    new int[]{borderHeight + 1, 1, getHeight() - 1, getHeight() - 1 - borderHeight}, 4);
            g.setColor(raised ? Color.BLACK : Color.WHITE);
            g.fillPolygon(new int[]{borderWidth + 1, getWidth() - borderWidth - 2, getWidth() - 2, 1},
                    new int[]{getHeight() - borderHeight - 1, getHeight() - borderHeight - 1, getHeight() - 1, getHeight() - 1
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
			if ("JPEG".equalsIgnoreCase(format) || "JPG".equalsIgnoreCase(format)) {
			    readJPEG(reader);
			} else { // not a JPEG, read normally
			    drawImageFromReader(reader);
			}
			backgroundColor = ColorDatabase.lookUp("white");
			// TODO Read multiple images if present?
			// How to coordinate this and ImageList?
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

    private void drawImageFromReader(ImageReader reader) throws IOException {
	BufferedImage pre = reader.read(0);
	image = new BufferedImage(pre.getWidth(), pre.getHeight(), BufferedImage.TYPE_INT_ARGB);
	image.createGraphics().drawImage(pre, 0, 0, null);
	image.createGraphics().dispose();
	pre = null;
    }

    private void readJPEG(ImageReader reader) throws IOException {
	Iterator<ImageTypeSpecifier> imageTypes = reader.getImageTypes(0);
	if (imageTypes.hasNext() && imageTypes.next().getNumBands() == 3) { 
	    // RGB JPEG, read normally
	    drawImageFromReader(reader);
	} else { // might be CMYK or YCCK JPEG
	    IIOMetadata metadata = reader.getImageMetadata(0);
	    String metadataFormat =
		metadata.getNativeMetadataFormatName();
	    IIOMetadataNode iioNode = (IIOMetadataNode)
		metadata.getAsTree(metadataFormat);
	    NodeList children =
		iioNode.getElementsByTagName("app14Adobe");

	    if (children.getLength() > 0) { // has colorspace field		
		iioNode = (IIOMetadataNode) children.item(0);
		int transform = Integer.
		    parseInt(iioNode.getAttribute("transform"));
		Raster raster = reader.readRaster(0, reader.getDefaultReadParam());
		
		if (raster.getNumBands() == 4) { // CMYK or YCCK
		    image = createJPEG4(raster, transform);
		} else { // wrong number of channels
		    throw new RuntimeException("failed to process jpeg");
		}
	    } else { // no app14Adobe field, read normally
		drawImageFromReader(reader);
	    }	    
	}
    }

    /**
     * Java's ImageIO can't process 4-component images
     * and Java2D can't apply AffineTransformOp either,
     * so convert raster data to RGB.
     *
     * Technique due to Mark Stephens.
     *
     * Free for any use.
    */
    private static BufferedImage createJPEG4(Raster raster, int xform) {
	int w = raster.getWidth();
	int h = raster.getHeight();
	byte[] rgb = new byte[w*h*3];

	// if Adobe_APP14 and transform == 2 then YCCK else CMYK
	if (xform == 2) { // YCCK

	    float[] Y = raster.getSamples(0, 0, w, h, 0, (float[]) null);
	    float[] Cb = raster.getSamples(0, 0, w, h, 1, (float[]) null);
	    float[] Cr = raster.getSamples(0, 0, w, h, 2, (float[]) null);
	    float[] K = raster.getSamples(0, 0, w, h, 3, (float[]) null);

	    for (int i = 0, imax = Y.length, base = 0; i < imax; i++, base += 3) {
		float k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i], cr = 255 - Cr[i];

		double val = y + 1.402*(cr - 128) - k;
		val = (val - 128) * .65f + 128;
		rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);

		val = y - 0.34414*(cb - 128) - 0.71414*(cr - 128) - k;
		val = (val - 128) * .65f + 128;
		rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);

		val = y + 1.772*(cb - 128) - k;
		val = (val - 128) * .65f + 128;
		rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);
	    }

	} else { // CMYK

	    int[] C = raster.getSamples(0, 0, w, h, 0, (int[]) null);
	    int[] M = raster.getSamples(0, 0, w, h, 1, (int[]) null);
	    int[] Y = raster.getSamples(0, 0, w, h, 2, (int[]) null);
	    int[] K = raster.getSamples(0, 0, w, h, 3, (int[]) null);

	    for (int i = 0, imax = C.length, base = 0; i < imax; i++, base += 3) {
		int c = 255 - C[i];
		int m = 255 - M[i];
		int y = 255 - Y[i];
		int k = 255 - K[i];
		float kk = k/255f;

		rgb[base] = (byte) (255 - Math.min(255f, c * kk + k));
		rgb[base + 1] = (byte) (255 - Math.min(255f, m * kk + k));
		rgb[base + 2] = (byte) (255 - Math.min(255f, y * kk + k));
	    }
	}

	// from other image types we know InterleavedRasters can be
	// manipulated by AffineTransformOp, so create one of those.
	raster = Raster.createInterleavedRaster(new DataBufferByte(rgb, rgb.length),
						w, h, w*3, 3, new int[] {0, 1, 2}, null);

	ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	ColorModel cm = new ComponentColorModel(cs, false, true,
	  Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	return new BufferedImage(cm, (WritableRaster) raster, true, null);
    }

    public MagickImage resized(int newWidth, int newHeight){
        
        // Copied from image_voodoo
        MagickImage result = new MagickImage(newWidth, newHeight);
        Graphics2D graphics = result.getImage().createGraphics();
        
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        double widthRatio = ((float) newWidth)/this.getWidth();
        double heightRatio = ((float) newHeight)/this.getHeight();
        
        graphics.drawRenderedImage(this.getImage(), AffineTransform.getScaleInstance(widthRatio, heightRatio));
        
        graphics.dispose();

        result.setFormat(this.getFormat());
        return result;
    }
    
    public MagickImage resized(double ratio) {
        return resized((int) Math.ceil(ratio*this.getWidth()),(int) Math.ceil(ratio*this.getHeight()));
    }

    public void rotate(double degrees) {
        transform(AffineTransform.getRotateInstance(Math.toRadians(degrees)));
    }

    public void setBackgroundColor(PixelPacket bg){
        this.backgroundColor = bg;
    }

    public void setBlur(double blur){
        this.blur = blur;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }

    public void setImage(BufferedImage img){
        this.image = img;
    }

    public void setImageFromConvolve(BufferedImage convolved, int width) {
        int halfWidth = width/2;
        int w = convolved.getWidth() - 2*halfWidth;
        int h = convolved.getHeight() - 2*halfWidth;
        this.image = convolved.getSubimage(halfWidth, halfWidth, w, h);
    }

    public void setMatte(boolean matte) {
        this.matte = matte;
    }

    public void storePixels(int x, int y, int width, int height, Object[] pixels){
        
        
        double[] data = new double[pixels.length * 4];
        
        for(int i=0; i<pixels.length; i++){
            double[] pixelData = ((PixelPacket) pixels[i]).toDoubleArray();
            data[4*i] = pixelData[0];
            data[4*i+1] = pixelData[1];
            data[4*i+2] = pixelData[2];
            data[4*i+3] = pixelData[3];
        }
        
        this.getImage().getRaster().setPixels(x, y, width, height, data);
        this.getImage().createGraphics().dispose();
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

    public String fileType(String file) {
	String type;
	int split = file.indexOf(':');
	if (split == -1) {
	    type = file.replaceFirst("^.*[.]([^.]+)", "$1").toUpperCase();
	} else {
	    type = file.substring(0, split).toUpperCase();
	}
	if (type.equals("JPG")) {
	    type = "JPEG";
	}
	return type;
    }

    public String filePath(String file) {
	String path;
	int split = file.indexOf(':');
	if (split == -1) {
	    path = file;
	} else {
	    path = file.substring(split + 1);
	}
	return path;
    }

    public void write(String file) {
        String type = fileType(file);
	String path = filePath(file);
        try {
            // TODO More robust type handling.
            FileOutputStream stream = new FileOutputStream(path);
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
                Graphics2D graphics = (Graphics2D) image.createGraphics();
                graphics.setBackground(Color.WHITE);
                graphics.clearRect(0, 0, getWidth(), getHeight());
                try {
                    graphics.drawImage( this.getImage(),
                                        0,
                                        0,
                                        null);
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

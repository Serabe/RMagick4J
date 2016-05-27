package magick4j;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.List;

public class DrawInfo implements Cloneable {

    private PixelPacket fill = ColorDatabase.queryDefault("black");
    private double fillOpacity = 1.0;
    private Pattern fillPattern = null;
    private int fillRule = GeneralPath.WIND_EVEN_ODD;
    private String fontFamily = "SansSerif";
    private int fontWeight;
    private Gravity gravity = Gravity.FORGET;
    private double pointSize = 12.0;
    private AffineTransform spaceTransformation = new AffineTransform(1,0,0,1,0,0);
    private PixelPacket stroke = ColorDatabase.queryDefault("none");
    private boolean strokeAntialias = true;
    private double[] strokeDashArray;
    private int strokeLinecap = BasicStroke.CAP_BUTT;
    private int strokeLinejoin = BasicStroke.JOIN_MITER;
    private float strokeMiterLimit = 10f;
    private Pattern strokePattern = null;
    private double strokeWidth = 1.0;

    public void annotate(MagickImage image, double width, double height, double iniX, double iniY, String text) {

        text = new TextFormatter(image).format(text);
        
        if (width == 0 && height == 0) {
            width = image.getWidth();
            height = image.getHeight();
        }
        
        TypeMetrics mlm = this.getMultilineTypeMetrics(text, image);
        
        Graphics2D graphics = createGraphics(image);
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            updateFont(graphics);
            
            String[] strings = text.split("\n");
            
            double y = iniY;


            switch (gravity) {
                case CENTER:
                case EAST:
                case WEST:
                    y += 0.5 * (height - mlm.getHeight());
                    break;
//                case NORTH:
//                case NORTH_EAST:
//                case NORTH_WEST:
//                    DO NOTHING
//                    break;
                case SOUTH:
                case SOUTH_EAST:
                case SOUTH_WEST:
                    y += height - mlm.getHeight();
                    break;
            }
            
            y += mlm.getAscent();

            for(int i = 0; i<strings.length; i++){

                TypeMetrics slm = this.getTypeMetrics(strings[i], image);
                
                double x = iniX;
                
                // x gravity
                switch (gravity) {
                    case CENTER:
                    case NORTH:
                    case SOUTH:
                        //x += 0.5 * (width - metrics.stringWidth(text));
                        x += 0.5 * (width - slm.getWidth());
                        break;
                    case EAST:
                    case NORTH_EAST:
                    case SOUTH_EAST:
                        //x = width - metrics.stringWidth(text) - x;
                        x += width - slm.getWidth();
                        break;
                }
                
                // TODO If we have a fill and a stroke, we may need to make a path
                graphics.setColor(fill.toColor());

                graphics.rotate(this.currentRotation, iniX + width / 2, iniY + height / 2);
                graphics.drawString(strings[i], (float) x, (float) y);

                y += slm.getHeight();
            }
                
        } finally {
            graphics.dispose();
        }
    }

    @Override
    public DrawInfo clone() {
        try {
            DrawInfo clone = (DrawInfo) super.clone();
            clone.setSpaceTransformation((AffineTransform) this.getSpaceTransformation().clone());
            // TODO Mutable fill and stroke?
            return clone;
        } catch (Exception e) {
            throw Thrower.throwAny(e);
        }
    }

    private Graphics2D createGraphics(final MagickImage image) {
        Graphics2D graphics = image.getImage().createGraphics();
        try {
            return graphics;
        } catch (Exception e) {
            graphics.dispose();
            throw Thrower.throwAny(e);
        }
    }

    public void draw(MagickImage image, List<Command> commands) {
        Graphics2D graphics = image.getImage().createGraphics();
        try {
            DrawContext context = new DrawContext(this, image);
            try {
                for (Command command : commands) {
                    command.perform(context);
                }
            } finally {
                context.dispose();
            }
        } finally {
            graphics.dispose();
        }
    }

    public PixelPacket getFill() {
        return fill;
    }

    public double getFillOpacity() {
        return fillOpacity;
    }
    
    public Pattern getFillPattern() {
        return this.fillPattern;
    }
    
    public int getFillRule(){
        return this.fillRule;
    }

    public String getFontFamily() {
        return fontFamily;
    }
    
    public TypeMetrics getMultilineTypeMetrics(String string, MagickImage image) {
        TypeMetrics metrics = new TypeMetrics();
        if (image == null) {
            image = new MagickImage(1, 1);
        }
        Graphics2D graphics = createGraphics(image);
        try {
            updateFont(graphics);
            
            String[] line = string.split("\n");
            
            for(int i=0; i < line.length; i++)
                metrics.compose(TypeMetrics.fromFontMetrics(graphics.getFontMetrics(), line[i]));
            
        } finally {
            graphics.dispose();
        }
        return metrics;
    }
    
    public AffineTransform getSpaceTransformation() {
        return this.spaceTransformation;
    }

    public PixelPacket getStroke() {
        return stroke;
    }

    public double[] getStrokeDashArray() {
        return strokeDashArray;
    }
    
    public int getStrokeLinecap() {
        return this.strokeLinecap;
    }
    
    public int getStrokeLinejoin() {
        return this.strokeLinejoin;
    }
    
    public float getStrokeMiterLimit() {
        return this.strokeMiterLimit;
    }

    public Pattern getStrokePattern(){
        return this.strokePattern;
    }
    
    public double getStrokeWidth() {
        return strokeWidth;
    }

    public TypeMetrics getTypeMetrics(String string, MagickImage image) {
        TypeMetrics metrics = new TypeMetrics();
        if (image == null) {
            image = new MagickImage(1, 1);
        }
        Graphics2D graphics = createGraphics(image);
        try {
            updateFont(graphics);
            metrics = TypeMetrics.fromFontMetrics(graphics.getFontMetrics(), string);
        } finally {
            graphics.dispose();
        }
        return metrics;
    }

    public boolean isStrokeAntialias() {
        return strokeAntialias;
    }

    private double currentRotation;
    public void rotate(double rotation) {
        this.spaceTransformation.concatenate(AffineTransform.getRotateInstance(Math.toRadians(rotation)));
        this.currentRotation += Math.toRadians(rotation);
    }

    public void scale(double scaleX, double scaleY) {
        this.spaceTransformation.concatenate(AffineTransform.getScaleInstance(scaleX, scaleY));
    }

    public void setFill(Pattern fill){
        this.fillPattern = fill;
    }
    
    public void setFill(PixelPacket fill) {
        // TODO Clone?
        this.fill = fill;
        this.fillPattern = null;
    }

    public void setFillOpacity(double fillOpacity) {
        this.fillOpacity = fillOpacity;
    }
    
    public void setFillRule(int fillRule){
        this.fillRule = fillRule;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public void setFontWeight(int fontWeight) {
        this.fontWeight = fontWeight;
    }

    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    public void setPointSize(double pointSize) {
        this.pointSize = pointSize;
    }

    private void setSpaceTransformation(AffineTransform spaceTransformation) {
        this.spaceTransformation = spaceTransformation;
    }

    public void setStroke(Pattern color) {
        this.strokePattern = color;
    }

    public void setStroke(PixelPacket stroke) {
        // TODO Clone?
        this.stroke = stroke;
        this.strokePattern = null;
    }

    public void setStrokeAntialias(boolean antialias) {
        this.strokeAntialias = antialias;
    }

    public void setStrokeDashArray(double... lengths) {
        this.strokeDashArray = lengths;
    }

    public void setStrokeLinecap(int linecap) {
        this.strokeLinecap = linecap;
    }

    public void setStrokeLinejoin(int linejoin) {
        this.strokeLinejoin = linejoin;
    }
    
    public void setStrokeMiterLimit(float miterLimit) {
        this.strokeMiterLimit = miterLimit/2; //DO NOT CHANGE THIS. It converts the miterLimit from ImageMagick to Java.
    }
    
    public void setStrokePattern(Pattern pattern){
        this.strokePattern = pattern;
    }

    public void setStrokeWidth(double width) {
        this.strokeWidth = width;
    }
    
    public void skewX(double degrees){
        AffineTransform af = new AffineTransform(1d, 0d, Math.tan(Math.toRadians(degrees)), 1d, 0d, 0d);
        this.spaceTransformation.concatenate(af);
    }
    
    public void skewY(double degrees){
        AffineTransform af = new AffineTransform(1d, Math.tan(Math.toRadians(degrees)), 0d, 1d, 0d, 0d);
        this.spaceTransformation.concatenate(af);
    }

    public void translate(double x, double y) {
        this.spaceTransformation.concatenate(AffineTransform.getTranslateInstance(x, y));
    }

    public void setText(MagickImage image, double iniX, double iniY, String text) {
	    // TODO: It is a limited implements for use redmine.
    	// For full support, need to implement these methods.
    	// setTextAlign
    	// setTextAnchor
    	// setTextAntialias
    	// setTextUndercolor
    	text = new TextFormatter(image).format(text);
	        
    	int width = image.getWidth();
    	int height = image.getHeight();
	        
    	TypeMetrics mlm = this.getMultilineTypeMetrics(text, image);
	        
    	Graphics2D graphics = createGraphics(image);
    	try {
    		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    		updateFont(graphics);
    		
    		String[] strings = text.split("\n");
	            
    		double y = iniY;
	            	            
    		switch (gravity) {
    		    case CENTER:
    		    case EAST:
    		    case WEST:
    		    	y += 0.5 * (height - mlm.getHeight());
    		    	break;
	//          case NORTH:
	//          case NORTH_EAST:
	//          case NORTH_WEST:
	//              DO NOTHING
	//                    break;
    		    case SOUTH:
    		    case SOUTH_EAST:
    		    case SOUTH_WEST:
    		    	y += height - mlm.getHeight();
    		    	break;
    		}
	            
    		for(int i = 0; i<strings.length; i++){
	                
    			TypeMetrics slm = this.getTypeMetrics(strings[i], image);
	                
    			double x = iniX;
	                
    			// x gravity
    			switch (gravity) {
    			    case CENTER:
    			    case NORTH:
    			    case SOUTH:
    			    	//x += 0.5 * (width - metrics.stringWidth(text));
    			    	x += 0.5 * (width - slm.getWidth());
    			    	break;
    			    case EAST:
    			    case NORTH_EAST:
    			    case SOUTH_EAST:
    			    	//x = width - metrics.stringWidth(text) - x;
    			    	x += width - slm.getWidth();
    			    	break;
    			}
	                
    			// TODO If we have a fill and a stroke, we may need to make a path
    			graphics.setColor(fill.toColor());
	                
    			graphics.drawString(strings[i], (float) x, (float) y);
	                
    			y += slm.getHeight();
    		}
	                
    	} finally {
    		graphics.dispose();
    	}
    }

	private void updateFont(Graphics2D graphics) {
        Font font = new Font(fontFamily, fontWeight >= 700 ? Font.BOLD : Font.PLAIN,
                (int) pointSize).deriveFont((float) pointSize);
        
        graphics.setFont(font);
    }
}

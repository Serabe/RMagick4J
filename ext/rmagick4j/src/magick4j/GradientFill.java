package magick4j;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.image.WritableRaster;

public class GradientFill {
    private final PixelPacket endColor;
    private final PixelPacket startColor;
    private final double x1;
    private final double x2;
    private final double y1;
    private final double y2;

    public GradientFill(double x1, double y1, double x2, double y2, PixelPacket startColor, PixelPacket endColor) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public void fill(MagickImage image) {
        // I have to admit to looking at rmfill.c for some reference on how this works.
        Paint paint=null;
        if (Math.abs(x2 - x1) < 0.5) {
            if (Math.abs(y2 - y1) < 0.5) {
                // TODO point
                pointFill(image);
            } else {
                // vertical
                paint = new GradientPaint((float) x1, (float) 0, startColor.toColor(), image.getWidth(), 0, endColor.toColor(), true);
            }
        } else if (Math.abs(y2 - y1) < 0.5) {
            // horizontal
            paint = new GradientPaint((float) 0, (float) y1, startColor.toColor(), 0, image.getHeight(), endColor.toColor(), true);
        } else {
            // TODO diagonal
            paint = null;
        }
        
        Graphics2D graphics = image.getImage().createGraphics();
        try {
            if(paint != null){
                graphics.setPaint(paint);
                graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
            }
        } finally {
            graphics.dispose();
        }
        
    }
    
    
    private void pointFill(MagickImage image){
        int columns = image.getWidth(), rows = image.getHeight();

        // The steps are the distance from the point to the right, lower corner.
        double steps = Math.sqrt((columns-this.x1)*(columns-this.x1) + (rows-this.y1)*(rows-this.y1));

        // Calculates the step for each color.
        double stepRed   = (this.endColor.getRed()     - this.startColor.getRed()  )/steps;
        double stepBlue  = (this.endColor.getBlue()    - this.startColor.getBlue() )/steps;
        double stepGreen = (this.endColor.getGreen()   - this.startColor.getGreen())/steps;
        double stepAlpha = (this.endColor.getOpacity() - this.startColor.getOpacity())/steps;

        WritableRaster raster = image.getImage().getRaster();

        for(int y=0; y<rows; y++){

            for(int x=0; x<columns; x++){
                double distance = Math.sqrt((x-this.x1)*(x-this.x1) + (y-this.y1)*(y-this.y1) );
                if(distance > steps) distance = steps;

                double data[] = new double[4];
                data[0] = (this.startColor.getRed()   +(stepRed   * distance)) * 255;
                data[1] = (this.startColor.getGreen() +(stepGreen * distance)) * 255;
                data[2] = (this.startColor.getBlue()  +(stepBlue  * distance)) * 255;
                data[3] = 255.0; // Taken from the rmfill.c
                raster.setPixel(x, y, data);
            }

        }

        image.getImage().getGraphics().dispose();

    }
}

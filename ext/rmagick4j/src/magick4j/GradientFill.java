package magick4j;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.image.WritableRaster;

public class GradientFill {
    private PixelPacket endColor;
    private PixelPacket startColor;
    private double x1;
    private double x2;
    private double y1;
    private double y2;
    
    private double stepRed;
    private double stepGreen;
    private double stepBlue;

    public GradientFill(double x1, double y1, double x2, double y2, PixelPacket startColor, PixelPacket endColor) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.startColor = startColor;
        this.endColor = endColor;
    }
    
    private double[] calculatePixel(double distance){
        double[] data = new double[4];
        
        data[0] = (this.startColor.getRed()   +(this.stepRed   * distance)) * 255;
        data[1] = (this.startColor.getGreen() +(this.stepGreen * distance)) * 255;
        data[2] = (this.startColor.getBlue()  +(this.stepBlue  * distance)) * 255;
        data[3] = 255.0; // Taken from the rmfill.c file.
        
        return data;
    }
    
    private void calculateSteps(double steps){
        stepRed   = (this.endColor.getRed()     - this.startColor.getRed()  )/steps;
        stepBlue  = (this.endColor.getBlue()    - this.startColor.getBlue() )/steps;
        stepGreen = (this.endColor.getGreen()   - this.startColor.getGreen())/steps;
    }

    public void fill(MagickImage image) {
        // I have to admit to looking at rmfill.c for some reference on how this works.
        if (Math.abs(x2 - x1) < 0.5) {
            if (Math.abs(y2 - y1) < 0.5) {
                // TODO point
                pointFill(image);
            } else {
                // vertical
                verticalFill(image);
            }
        } else if (Math.abs(y2 - y1) < 0.5) {
            // horizontal
            horizontalFill(image);
        } else {
            // TODO: diagonal
            throw new UnsupportedOperationException("Not yet implemented diagonal gradient filling.");
        }
        
        image.getImage().createGraphics().dispose();
        
        
    }

    private void horizontalFill(MagickImage image) {
        int columns = image.getWidth(), rows = image.getHeight();
        
        double steps = Math.max(this.y1, rows - this.y1);
        
        // TODO: Is this if necessary?
        if(steps < 0){
            PixelPacket ppAux = this.startColor;
            this.startColor = this.endColor;
            this.endColor = ppAux;
            steps = -steps;
        }
        
        if(this.y1 < 0){
            steps -= this.y1;
        }
        
        this.calculateSteps(steps);
        
        WritableRaster raster = image.getImage().getRaster();
        
        // First, it iterates over the y, because if two
        // points have the same y value, then they have
        // the same color.
        
        for(int y = 0; y < rows; y++){
            double distance = Math.abs(this.y1 - y);
            double[] data = this.calculatePixel(distance);
            
            for(int x = 0; x < columns; x++){
                raster.setPixel(x, y, data);
            }
        }
    }
    
    // Radial gradient.
    private void pointFill(MagickImage image){
        int columns = image.getWidth(), rows = image.getHeight();

        // The steps are the distance from the point to the right, lower corner.
        double steps = Math.sqrt((columns-this.x1)*(columns-this.x1) + (rows-this.y1)*(rows-this.y1));

        this.calculateSteps(steps);

        WritableRaster raster = image.getImage().getRaster();

        // Nested fors mean for each point. 
        for(int y=0; y<rows; y++){

            for(int x=0; x<columns; x++){
                double distance = Math.sqrt((x-this.x1)*(x-this.x1) + (y-this.y1)*(y-this.y1) );
                if(distance > steps) distance = steps;

                double data[] = this.calculatePixel(distance);
                raster.setPixel(x, y, data);
            }

        }

    }

    private void verticalFill(MagickImage image){
        // Renaming for convenience.
        int columns = image.getWidth(), rows = image.getHeight();
        
        // Calculate the steps.
        
        double steps = Math.max(this.x1, columns - this.x1);
        
        // Fix the steps value.
        
        // TODO: Is this if necessary?
        if(steps < 0){
            PixelPacket ppAux = this.startColor;
            this.startColor = this.endColor;
            this.endColor = ppAux;
            steps = -steps;
        }
        
        if(this.x1 < 0){
            steps -= this.x1;
        }
        
        this.calculateSteps(steps);
        
        WritableRaster raster = image.getImage().getRaster();
        
        // First, it iterates over the x, because if two
        // points have the same x value, then they have
        // the same color.
        
        for(int x = 0; x < columns; x++){
            double distance = Math.abs(this.x1-x);
            double[] data = this.calculatePixel(distance);
            
            for(int y = 0; y < rows; y++){
                raster.setPixel(x, y, data);
            }
            
        }
    }
}

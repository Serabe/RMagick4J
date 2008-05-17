package magick4j;

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
        
        data[0] = ((double)this.startColor.getRed())   +(this.stepRed   * distance);
        data[1] = ((double)this.startColor.getGreen()) +(this.stepGreen * distance);
        data[2] = ((double)this.startColor.getBlue())  +(this.stepBlue  * distance);
        data[3] = 255.0; // Taken from the rmfill.c file.
        
        return data;
    }
    
    private void calculateSteps(double steps){
        stepRed   = (((double)this.endColor.getRed()     - this.startColor.getRed()  ))/steps;
        stepBlue  = (((double)this.endColor.getBlue()    - this.startColor.getBlue() ))/steps;
        stepGreen = (((double)this.endColor.getGreen()   - this.startColor.getGreen()))/steps;
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
            double m = (this.y2-this.y1) / (this.x2-this.x1);
            double mainDiagonal = image.getHeight()/image.getWidth();
            double b = this.y1 - (m*this.x1);
            
            if(Math.abs(m) <= mainDiagonal){
                // Why is this called vertical, when the line more horizontal
                // than vertical? If verticalFill is called verticalFill then
                // this must be called horizontalDiagonalFill.
                verticalDiagonalFill(image, m, b);
            }else{
                horizontalDiagonalFill(image, m, b);
            }
            
        }
        
        image.getImage().createGraphics().dispose();
        
        
    }

    private void horizontalDiagonalFill(MagickImage image, double m, double b) {
        int columns = image.getWidth(), rows = image.getHeight();
        
        
        // Calculate steps value.
        double steps = 0.0;
        
        double distance1 = -b/m;
        double distance2 = (rows - b)/m;
        
        if( distance1 < 0 && distance2 < 0){
            steps += Math.max(  Math.abs(distance1),
                                Math.abs(distance2)
                             );
        }else if(distance1 > columns && distance2 > columns){
            steps += Math.max(  Math.abs(distance1 - columns),
                                Math.abs(distance2 - columns)
                             );
        }
        
        steps += Math.max(  Math.max(distance1, columns-distance1),
                            Math.max(distance2, columns-distance2)
                         );
        
        // Things to do before entering the for loops.
        this.calculateSteps(steps);
        
        WritableRaster raster = image.getImage().getRaster();
        
        // For each pixel
        for(int y = 0; y < rows; y++){
            
            for(int x = 0; x < columns; x++){
                double distance = Math.abs(x - ((y-b)/m));
                
                double[] data = this.calculatePixel(distance);
                
                raster.setPixel(x, y, data);
            }
            
        }
    }

    private void horizontalFill(MagickImage image) {
        int columns = image.getWidth(), rows = image.getHeight();
        
        // Calculate the steps value.
        double steps = Math.max(this.y1, rows - this.y1);
        
        if(this.y1 < 0){
            steps -= this.y1;
        }
        
        // Things to do before entering the for loops.
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

        // For each pixel. 
        for(int y=0; y<rows; y++){

            for(int x=0; x<columns; x++){
                double distance = Math.sqrt((x-this.x1)*(x-this.x1) + (y-this.y1)*(y-this.y1) );
                if(distance > steps) distance = steps;

                double data[] = this.calculatePixel(distance);
                raster.setPixel(x, y, data);
            }

        }

    }

    private void verticalDiagonalFill(MagickImage image, double m, double b) {
        // Renaming for convenience.
        int columns = image.getWidth(), rows = image.getHeight();
        
        double steps = 0.0;
        
        double distance1 = b;
        double distance2 = m*columns + b;
        
        if( distance1 < 0 && distance2 < 0){
            steps += Math.max(  Math.abs(distance1),
                                Math.abs(distance2)
                             );
        }else if(distance1 > rows && distance2 > rows){
            steps += Math.max(  Math.abs(distance1 - rows),
                                Math.abs(distance2 - rows)
                             );
        }
        
        steps += Math.max(  Math.max(distance1, rows-distance1),
                            Math.max(distance2, rows-distance2)
                         );
        
        // Fix the steps value.
        
        this.calculateSteps(steps);
        
        WritableRaster raster = image.getImage().getRaster();
        
        for(int y = 0; y < rows; y++){
            
            for(int x = 0; x < columns; x++){
                
                double distance = Math.abs(y - (m*x + b));
                
                double[] data = this.calculatePixel(distance);
                
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

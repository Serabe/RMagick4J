/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magick4j;

import java.awt.image.WritableRaster;

/**
 *
 * @author serabe
 */
public class TextureFill {

    private MagickImage texture;
    
    public TextureFill(MagickImage image){
        this.texture = image;
    }
    
    public void fill(MagickImage image){
        int columnsBackground  = this.texture.getWidth(),
            rowsBackground     = this.texture.getHeight(),
            columns             = image.getWidth(),
            rows                = image.getHeight();
        
        WritableRaster textureRaster = this.texture.getImage().getRaster(),
                       imageRaster = image.getImage().getRaster();
        
        for(int y = 0; y < rows; y++){
            
            for(int x = 0; x < columns; x++){
                double[] data = new double[4];
                double[] texturePixel = textureRaster.getPixel(x%columnsBackground, y%rowsBackground, (double[]) null);
                data[0] = texturePixel[0];
                data[1] = texturePixel[1];
                data[2] = texturePixel[2];
                data[3] = 255.0;
                imageRaster.setPixel(x, y, data);
            }
        }
    }
}

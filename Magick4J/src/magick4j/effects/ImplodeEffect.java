package magick4j.effects;

import java.awt.image.WritableRaster;
import magick4j.MagickImage;
import magick4j.exceptions.OptionException;

import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import static magick4j.Constants.MagickPI;

/**
 *
 * @author serabe
 */
public class ImplodeEffect extends BasicEffect{

    protected double amount;

    public ImplodeEffect(double amount){
        this.amount = amount;
    }

    @Override
    protected MagickImage effect(MagickImage image) throws OptionException {
        MagickImage implode = image.createCompatible();
        int w = image.getWidth(), h = image.getHeight();
        int y, x;

        double[] scale = {1.0,1.0};
        double[] center = {0.5*w,0.5*h};
        double[] pixel = new double[4];
        double[] delta = new double[2];
        double[] q = new double[w*4];
        double dist, factor, radius = center[0];

        if( w > h){
            scale[1] = ((double) w)/((double) h);
        }else if(h > w){
            scale[0] = ((double) h)/((double) w);
            radius = center[1];
        }

        double radius2 = radius*radius;

        WritableRaster o = image.getImage().getRaster();
        WritableRaster d = implode.getImage().getRaster();

        for(y = 0; y < h; y++){
            pixel[0] = pixel[1] = pixel[2] = pixel[3] = 0.0;
            delta[1] = scale[1]*(y-center[1]);

            for(x = 0; x < w; x++){
                delta[0] = scale[0]*(x-center[0]);
                dist = delta[0]*delta[0]+delta[1]*delta[1];

                if(dist >= radius2){
                    pixel = o.getPixel(x, y, pixel);
                } else {
                    factor = 1.0;

                    if(dist > 0.0)
                        factor = pow(sin( MagickPI*sqrt(dist)/radius/2 ),-this.amount);

                    pixel = o.getPixel((int)(factor*delta[0]/scale[0]+center[0]), (int)(factor*delta[1]/scale[1]+center[1]), pixel);

                }

                System.arraycopy(pixel, 0, q, x*4, 4);

            }

            d.setPixels(0, y, w, 1, q);
        }

        return implode;
    }

}
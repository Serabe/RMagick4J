package magick4j.effects;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import magick4j.MagickImage;
import magick4j.exceptions.OptionException;

import static java.lang.Math.exp;
import static magick4j.Constants.KernelRank;
import static magick4j.Constants.MagickSQ2PI;
import static magick4j.Constants.magickSigma;
import static magick4j.Constants.OpaqueOpacity;
import static magick4j.Quantum.roundToQuantum;

/**
 *
 * @author serabe
 */
public class BlurEffect extends BasicEffect{

    private double radius;
    private double sigma;

    public BlurEffect(double radius, double sigma){
        this.radius = radius;
        this.sigma = sigma;
    }

    @Override
    protected MagickImage effect(MagickImage image) throws OptionException {
        int width = this.getOptimalKernelWidth1D(this.radius, this.sigma);
        int halfWidth = width/2;
        int h = image.getHeight(), w = image.getWidth();
        int pw = (w+2*halfWidth), ph = (h+2*halfWidth);
        int base,i,x,y;
        double bias = 0.0;
        double[] kernel = this.getBlurKernel(width);
        double[] p = new double[pw*4];
        double[] q = new double[w*4];
        double[] pixel = new double[4];

        MagickImage blur = image.createCompatible();
        BufferedImage conv = image.expandBorders(0, halfWidth, 0, halfWidth);

        WritableRaster o = conv.getRaster();
        WritableRaster d = blur.getImage().getRaster();

        /*
         * Blur Rows.
         */

        for(y = 0; y < h; y++){

            p = o.getPixels(0, y, pw, 1, p);

            for(x = 0; x < w; x++){
                pixel[0] = pixel[1] = pixel[2] = 0;
                pixel[3] = OpaqueOpacity;

                //TODO Implement Channels.
                for(i = 0; i < width; i++){
                    base = 4*(x+i);
                    pixel[0] += kernel[i]*p[base+0];
                    pixel[1] += kernel[i]*p[base+1];
                    pixel[2] += kernel[i]*p[base+2];
                }

                base = 4*x;

                q[base+0] = roundToQuantum(pixel[0]+bias);
                q[base+1] = roundToQuantum(pixel[1]+bias);
                q[base+2] = roundToQuantum(pixel[2]+bias);
                q[base+3] = 255.0;
            }

            d.setPixels(0, y, w, 1, q);
        }

        /*
         * Blur columns.
         */

        p = new double[ph*4];
        q = new double[h*4];

        o = null;
        conv = null;

        conv = blur.expandBorders(halfWidth, 0, halfWidth, 0);
        o = conv.getRaster();
        
        for(x = 0; x < w; x++){
            
            p = o.getPixels(x, 0, 1, ph, p);
            
            for(y = 0; y < h; y++){
                pixel[0] = pixel[1] = pixel[2] = 0;
                pixel[3] = OpaqueOpacity;

                //TODO Implement Channels.
                for(i = 0; i < width; i++){
                    base = 4*(y+i);
                    pixel[0] += kernel[i]*p[base+0];
                    pixel[1] += kernel[i]*p[base+1];
                    pixel[2] += kernel[i]*p[base+2];
                }

                base = 4*y;

                q[base+0] = roundToQuantum(pixel[0]+bias);
                q[base+1] = roundToQuantum(pixel[1]+bias);
                q[base+2] = roundToQuantum(pixel[2]+bias);
                q[base+3] = 255.0;
            }
            
            d.setPixels(x, 0, 1, h, q);
        }

        return blur;
    }

    public double[] getBlurKernel(int width){
        double[] kernel = new double[width];
        double alpha, normalize;
        double ms = magickSigma(this.sigma);
        double div  = 2.0*KernelRank*KernelRank*ms*ms;
        double div2 = MagickSQ2PI*this.sigma;
        
        int i;

        long bias   =  KernelRank* ((long) width/2);

        for(i = (int) -bias; i <= bias; i++){
            alpha = exp( (-(double) i*i)/((double) div));
            kernel[(int) (((int) (i+bias))/KernelRank)] += alpha/div2;
        }

        normalize = 0.0;

        for(i = 0; i < width; i++)
            normalize += kernel[i];

        for(i = 0; i < width; i++)
            kernel[i] /= normalize;

        return kernel;
    }
}

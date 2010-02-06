package magick4j.effects;

import magick4j.Constants;
import magick4j.MagickImage;

import magick4j.PixelPacket;
import magick4j.exceptions.OptionException;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.exp;
import static java.lang.Math.round;

public abstract class BasicEffect {
    
    protected static MagickImage convolve(MagickImage image, int order, double[] kernel)
              throws OptionException{
        int width = order;
        
        if((width%2) == 0)
            throw new OptionException("Kernel width must be an odd number.");

        MagickImage convolve = image.clone();

        /*
         * Normalize kernel
         */

        double[] normalKernel = new double[width*width];
        double gamma = 0.0;
        for(int i=0; i<(long) width*width; i++)
            gamma += kernel[i];
        gamma = 1.0/ ((abs(gamma)< Constants.MagickEpsilon) ? 1.0 : gamma);
        for(int i=0; i<(long) width*width; i++)
            normalKernel[i] = gamma*kernel[i];

        // Convolve

        double bias = 0.0; // image.getBias();

        int x,y,j,v,u;

        double[] k = normalKernel;

        PixelPacket pixel;

        for(y=0; y < image.getHeight(); y++){
            PixelPacket[][] p = image.getPixels(-((int) width/2),y-((int) width/2), image.getWidth()+width, width);
            PixelPacket[][] q = convolve.getPixels(0, y, convolve.getWidth(), 1);

            for(x=0; x < image.getWidth(); x++){
                pixel = new PixelPacket(0,0,0);
                j=0;

                double alpha;
                gamma=0.0;

                for(v=0; v < width; v++){
                    for(u=0; u < width; u++){
                        alpha = (Constants.QuantumScale*(Constants.QuantumRange-p[x+v][u].getOpacity()));
                        double c = k[v*width+u]*alpha;
                        pixel.setRed((int) round(pixel.getRed() + c*p[x+v][u].getRed()));
                        pixel.setGreen((int) round(pixel.getGreen() + c*p[x+v][u].getGreen()));
                        pixel.setBlue((int) round(pixel.getBlue() + c*p[x+v][u].getBlue()));
                        pixel.setOpacity((int) round(pixel.getOpacity() + k[v*width+u]*p[x+v][u].getOpacity()));
                        gamma += alpha;
                    }
                }

                gamma = 1.0/((abs(gamma) <= Constants.MagickEpsilon) ? 1.0 : gamma);
                q[x][1].setRed((int) round(gamma*pixel.getRed()+bias));
                q[x][1].setGreen((int) round(gamma*pixel.getGreen()+bias));
                q[x][1].setBlue((int) round(gamma*pixel.getBlue()+bias));
                q[x][1].setOpacity((int) round(pixel.getOpacity()+bias));
            }
        }

        return convolve;
    }

    protected static long getOptimalKernelWidth1D(double radius, double sigma){
	    if(radius > 0.0)
	        return (long) (2.0*ceil(radius)+1.0);
    	if(abs(sigma) <= Constants.MagickEpsilon)
            return 1;
        long width, u;
        double normalize, value;

        for(width=5;;){
            normalize = 0.0;
            for (u=(-width/2); u <= (width/2); u++)
                normalize+=exp(-((double) u*u)/(2.0*sigma*sigma))/(Constants.MagickSQ2PI*sigma);
            u=width/2;
            value=exp(-((double) u*u)/(2.0*sigma*sigma))/(Constants.MagickSQ2PI*sigma)/normalize;
            if ((long) (Constants.QuantumRange*value) <= 0L)
                break;
            width+=2;
        }
        return width-2;
	}
}

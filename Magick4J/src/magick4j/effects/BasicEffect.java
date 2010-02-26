package magick4j.effects;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import magick4j.Constants;
import magick4j.MagickImage;

import magick4j.exceptions.OptionException;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.exp;

public abstract class BasicEffect {
    
    public MagickImage apply(MagickImage image)
            throws OptionException{
        return this.effect(image);
    }
    
    public void modify(MagickImage image)
            throws OptionException{
        image.assimilate(this.effect(image));
    }

    protected abstract MagickImage effect(MagickImage image)
            throws OptionException;

    protected static MagickImage convolve(MagickImage image, int order, Kernel kernel)
              throws OptionException{

		if(kernel.getWidth()!=kernel.getHeight())
			throw new OptionException("Kernel must be a square matrix.");
		if(kernel.getWidth()%2 == 0)
			throw new OptionException("Kernel width must be an odd number.");

        double bias = 0.0; // Not used;
        int h = image.getHeight(), w = image.getWidth();
        MagickImage convolve = image.createCompatible();
        BufferedImage conv = image.getImageToConvolve(kernel.getWidth());

        int cw = conv.getWidth(), ch = conv.getHeight();
        BufferedImage dst = new BufferedImage(w, h, image.getImage().getType());


        /*
         * Normalize kernel.
         */

        float[] normal = kernel.getKernelData(null);

        float gamma = 0.0f;

        for(int i = 0; i<normal.length; i++){
            gamma += normal[i];
        }

        gamma = 1.0f/(abs(gamma) <= Constants.MagickEpsilon ? 1.0f : gamma);

        for(int i = 0; i<normal.length; i++)
            normal[i] *= gamma;

        // Some things to do before actual convolving.

        int kw = kernel.getWidth();
        double[][] p = new double[kw][cw*4];
        double[] q;

        WritableRaster d = dst.getRaster();
        WritableRaster o = conv.getRaster();

        // Convolve

		for(int j=1; j<kw; j++)
			p[j] = o.getPixels(0, j-1, cw, 1, p[j]);

        for(int y=0; y<h; y++){

            for(int j=1; j<kw; j++)
				System.arraycopy(p[j], 0, p[j-1], 0, cw*4);

			p[kw-1] = o.getPixels(0, y+kw-1, cw, 1, p[kw-1]);

            q = new double[w*4];
            for(int x=0; x<w; x++){
                
                double[] pixel = new double[4];
                pixel[0] = pixel[1] = pixel[2] = 0;
                pixel[3] = Constants.OpaqueOpacity;

                for(int v=0; v<kw; v++){
                    for(int u=0; u<kw; u++){
                        float e = normal[v*kw+u];
                        
                        pixel[0] += e*p[v][4*(x+u)+0];
                        pixel[1] += e*p[v][4*(x+u)+1];
                        pixel[2] += e*p[v][4*(x+u)+2];
                       
                    }
                }

                q[4*x+0] = roundToQuantum(pixel[0]+bias);
                q[4*x+1] = roundToQuantum(pixel[1]+bias);
                q[4*x+2] = roundToQuantum(pixel[2]+bias);
                q[4*x+3] = 255.0;
            }
            d.setPixels(0, y, w, 1, q);
        }

        convolve.setImage(dst);

        return convolve;
    }

    protected static int getOptimalKernelWidth1D(double radius, double sigma){
	if(radius > 0.0)
            return (int) (2.0*ceil(radius)+1.0);
    	if(abs(sigma) <= Constants.MagickEpsilon)
            return 1;
        int width, u;
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

    protected static int roundToQuantum(double d){
        if(d <= 0.0)
            return 0;
        if(d >= Constants.QuantumRange)
            return (int) Constants.QuantumRange;
        return (int) (d+0.5);
    }
}

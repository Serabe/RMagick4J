package magick4j.effects;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import magick4j.Constants;
import magick4j.MagickImage;

import magick4j.PixelPacket;
import magick4j.exceptions.OptionException;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.exp;
import static java.lang.Math.round;

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
        MagickImage convolve = image.createCompatible();
        BufferedImage conv = image.getImageToConvolve(kernel.getWidth());
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
        BufferedImage dst = op.createCompatibleDestImage(conv, conv.getColorModel());
        
        try{
            op.filter(conv.getData(), dst.getRaster());
        }catch (Exception ex){
            throw new OptionException("Fail convolve operation: "+ex.toString());
        }

        convolve.setImageFromConvolve(dst, kernel.getWidth());

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
}

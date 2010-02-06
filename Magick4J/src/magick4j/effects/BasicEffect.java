package magick4j.effects;

import magick4j.Constants;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.exp;

public abstract class BasicEffect {

    public static long getOptimalKernelWidth1D(double radius, double sigma){
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

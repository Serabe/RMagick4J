package magick4j;

/**
 *
 * @author serabe
 */
public class Constants {
    public static final int    KernelRank = 3;
    public static final double MagickEpsilon = 1.0e-10;
    public static final double MagickHuge = 1.0e12;
    public static final double MagickPI = 3.14159265358979323846264338327950288419716939937510;
    public static final double MagickSQ2PI = 2.50662827463100024161235523934010416269302368164062;
	public static final long   MaxMap = Math.round(Constants.QuantumRange);
    public static final double OpaqueOpacity = 0;
    public static final double QuantumRange = 255.0;
    public static final double QuantumScale = 1.0/QuantumRange;

    public static double magickSigma(double sigma){
        return (sigma <= MagickEpsilon) ? 1.0 : sigma;
    }
}

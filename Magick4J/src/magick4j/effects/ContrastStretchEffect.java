package magick4j.effects;

import java.awt.image.WritableRaster;
import magick4j.Constants;
import magick4j.MagickImage;
import magick4j.PixelPacket;
import magick4j.exceptions.OptionException;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static magick4j.Quantum.scaleQuantumToMap;
import static magick4j.Quantum.roundToQuantum;

/**
 *
 * @author serabe
 */
public class ContrastStretchEffect extends BasicEffect{
	protected double blackPoint;
	protected double currentWhitePoint;
	protected double whitePoint;

	public ContrastStretchEffect(double blackPoint, double whitePoint){
		this.blackPoint = blackPoint;
		this.whitePoint = whitePoint;
	}

	@Override
	protected MagickImage effect(MagickImage image) throws OptionException {
		
		MagickImage norm = image.createCompatible();

		int h = image.getHeight(), w = image.getWidth();
		int x, y, intensity, base;

		this.currentWhitePoint = w*h-this.whitePoint;

		double[] histogram  = new double[(int) (Constants.MaxMap+1)*4];
		double[] stretchMap = new double[(int) (Constants.MaxMap+1)*4];
		double[] p = new double[w*4];
		double[] q = new double[w*4];
		double[] black = new double[4];
		double[] white = new double[4];

		WritableRaster o = image.getImage().getRaster();
		WritableRaster d = norm.getImage().getRaster();

		/*
		 * Calculate histogram.
		 */
		for(y = 0; y < h; y++){

			p = o.getPixels(0, y, w, 1, p);

			// TODO Include Channels

			for(x = 0; x < w; x++){
				intensity = 4*((int) round(scaleQuantumToMap(PixelPacket.calculateIntensity(p, x*4))));
				histogram[intensity+0]++;
				histogram[intensity+1]++;
				histogram[intensity+2]++;
			}
		}

		/*
		 * Find boundaries.
		 */

		// TODO Include Channels.
		black[0] = black[1] = black[2] = black[3] = 0;
		white[0] = white[1] = white[2] = white[3] = (int) Constants.QuantumRange;

		black[0] = minLimit(histogram, 0);
		white[0] = maxLimit(histogram, 0);

		black[1] = minLimit(histogram, 1);
		white[1] = maxLimit(histogram, 1);

		black[2] = minLimit(histogram, 2);
		white[2] = maxLimit(histogram, 2);

		//black[3] = minLimit(histogram, 0);
		//white[3] = maxLimit(histogram, 0);

		histogram = null;

		/*
		 * Calculate the strecht map
		 */

		for(int i = 0; i<= Constants.MaxMap; i++){
			base = 4*i;
			// TODO Include Channels
			stretchMap[base+0] = stretch(i, black[0], white[0]);
			stretchMap[base+1] = stretch(i, black[1], white[1]);
			stretchMap[base+2] = stretch(i, black[2], white[2]);
			stretchMap[base+3] = 255;
		}

		/*
		 * Modify the image.
		 */

		for(y = 0; y < h; y++){

			p = o.getPixels(0, y, w, 1, p);
			System.arraycopy(p, 0, q, 0, w*4);

			for(x = 0; x < w; x++){
				base = x*4;

				// TODO Include Channels.
				q[base+0] = roundToQuantum(stretchMap[(int) (scaleQuantumToMap(p[base+0])*4+0)]);
				q[base+1] = roundToQuantum(stretchMap[(int) (scaleQuantumToMap(p[base+1])*4+1)]);
				q[base+2] = roundToQuantum(stretchMap[(int) (scaleQuantumToMap(p[base+2])*4+2)]);
				q[base+3] = 255.0;
			}

			d.setPixels(0, y, w, 1, q);
		}

		return norm;

	}

	private int maxLimit(double[] histogram, int offSet) {
		double intensity = 0.0;
		int x = 0;

		for(x = (int) Constants.MaxMap; x != 0 ; x--){
			intensity += histogram[x*4 + offSet];
			if(intensity > this.currentWhitePoint)
				break;
		}

		return x;
	}

	private int minLimit(double[] histogram, int offSet) {
		double intensity = 0.0;
		int x = 0;

		for(x = 0; x <= Constants.MaxMap; x++){
			intensity += histogram[x*4 + offSet];
			if(intensity > this.blackPoint)
				break;
		}

		return x;
	}

	private double stretch(int i, double min, double max) {
		if(i <= min){
			return 0.0;
		}else if(i >= max){
			return Constants.QuantumRange;
		}else if(min != max){
			return scaleQuantumToMap(Constants.MaxMap*(i-min)/(max-min));
		}else{
			return 0.0;
		}
	}

}

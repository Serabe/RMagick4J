package magick4j.effects;

import java.awt.image.WritableRaster;
import magick4j.Constants;
import magick4j.MagickImage;
import magick4j.exceptions.OptionException;

/**
 *
 * @author serabe
 */
public class SolarizeEffect extends BasicEffect{
	private double threshold;

	public SolarizeEffect(double threshold){
		this.threshold = threshold;
	}

	@Override
	protected MagickImage effect(MagickImage image) throws OptionException {
		MagickImage solarized = image.createCompatible();

		WritableRaster d = solarized.getImage().getRaster();
		WritableRaster o = image.getImage().getRaster();

		int h = image.getHeight(), w = image.getWidth();

		double[] p = new double[w*4];

		for(int y = 0; y<h; y++){
			p = o.getPixels(0, y, w, 1, p);

			for(int x = 0; x<w; x++){
				if(p[4*x+0] > threshold) p[4*x+0] = Constants.QuantumRange - p[4*x+0];
				if(p[4*x+1] > threshold) p[4*x+1] = Constants.QuantumRange - p[4*x+1];
				if(p[4*x+2] > threshold) p[4*x+2] = Constants.QuantumRange - p[4*x+2];
			}

			d.setPixels(0, y, w, 1, p);
		}

		return solarized;
	}

}

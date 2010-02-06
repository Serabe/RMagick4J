package magick4j.effects;

import java.awt.image.WritableRaster;
import magick4j.Constants;
import magick4j.MagickImage;
import magick4j.exceptions.OptionException;

/**
 *
 * @author serabe
 */
public class NegateEffect extends BasicEffect{

	private boolean grayscale;

	public NegateEffect(boolean grayscale){
		this.grayscale = grayscale;
	}

	@Override
	protected MagickImage effect(MagickImage image) throws OptionException {
		MagickImage neg = image.createCompatible();

		int w = image.getWidth(), h = image.getHeight();

		int base;

		WritableRaster o = image.getImage().getRaster();
		WritableRaster d = neg.getImage().getRaster();

		double[] p = new double[w*4];
		double[] q = new double[w*4];

		if(this.grayscale){
			for(int y = 0; y < h; y++){
				p = o.getPixels(0, y, w, 1, p);

				for(int x = 0; x < w; x++){
					base = x*4;
					if(p[base+0] != p[base+1] || p[base+1] != p[base+2]){
						System.arraycopy(p, base, q, base, 4);
					} else {
						q[base+0] = Constants.QuantumRange-p[base+0];
						q[base+1] = Constants.QuantumRange-p[base+1];
						q[base+2] = Constants.QuantumRange-p[base+2];
						q[base+3] = 255.0;
					}
				}

				d.setPixels(0, y, w, 1, q);
			}
		} else {
			for(int y = 0; y < h; y++){
				p = o.getPixels(0, y, w, 1, p);

				for(int x = 0; x < w; x++){
					base = x*4;

					q[base+0] = Constants.QuantumRange-p[base+0];
					q[base+1] = Constants.QuantumRange-p[base+1];
					q[base+2] = Constants.QuantumRange-p[base+2];
					q[base+3] = 255.0;
				}

				d.setPixels(0, y, w, 1, q);
			}
		}

		return neg;
	}


}

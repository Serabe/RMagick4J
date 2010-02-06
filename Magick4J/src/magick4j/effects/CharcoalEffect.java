package magick4j.effects;

import magick4j.MagickImage;
import magick4j.exceptions.OptionException;

/**
 *
 * @author serabe
 */
public class CharcoalEffect extends BasicEffect{

	private double radius;
	private double sigma;

	public CharcoalEffect(double radius, double sigma){
		this.radius = radius;
		this.sigma = sigma;
	}

	@Override
	protected MagickImage effect(MagickImage image) throws OptionException {
		MagickImage edge = (new EdgeEffect(this.radius)).effect(image);
		return edge.blurred(this.sigma, this.radius);
	}

}

package magick4j.effects;

import magick4j.Constants;
import magick4j.MagickImage;
import magick4j.exceptions.OptionException;


/**
 *
 * @author serabe
 */
public class NormalizeEffect extends ContrastStretchEffect{

	public NormalizeEffect(){
		super(0,Constants.QuantumRange);
	}

	@Override
	protected MagickImage effect(MagickImage image) throws OptionException {
		int size = image.getHeight()*image.getWidth();
		this.blackPoint = size*0.02;
		this.whitePoint = size*0.99;
		return super.effect(image);
	}
}

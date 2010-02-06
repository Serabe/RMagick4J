package magick4j.effects;

import magick4j.MagickImage;
import magick4j.exceptions.OptionException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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
		BufferedImage gray = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g = gray.createGraphics();
		g.drawImage(image.getImage(), 0, 0, null);
		g.dispose();
		MagickImage blur = (new BlurEffect(this.radius,this.sigma)).effect((new EdgeEffect(this.radius)).effect(new MagickImage(gray)));
		return (new NegateEffect(false)).effect((new NormalizeEffect()).effect(blur));
	}

}

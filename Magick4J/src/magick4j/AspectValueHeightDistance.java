package magick4j;

public class AspectValueHeightDistance extends ValueHeightDistance {

    public AspectValueHeightDistance(double value) {
	super(value);
    }

    public double getMagnitude(WidthDistance widthDistance, MagickImage image) {
	return widthDistance.getMagnitude(this, image);	
    }

    public double getMagnitude(RelativeWidthDistance widthDistance, MagickImage image) {
	return this.getValue();
    }

    public double getMagnitude(AspectValueWidthDistance widthDistance, MagickImage image) {
	return this.getValue();
    }

}

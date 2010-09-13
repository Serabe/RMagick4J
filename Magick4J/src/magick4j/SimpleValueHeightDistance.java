package magick4j;

public class SimpleValueHeightDistance extends ValueHeightDistance {

    public SimpleValueHeightDistance(double value) {
	super(value);
    }

    public double getMagnitude(WidthDistance widthDistance, MagickImage image) {
	return widthDistance.getMagnitude(this, image);	
    }

    public double getMagnitude(RelativeWidthDistance widthDistance, MagickImage image) {
	return this.getValue();
    }

    public double getMagnitude(SimpleValueWidthDistance widthDistance, MagickImage image) {
	return Math.min(Math.round(widthDistance.getValue()*image.getHeight()/image.getWidth()), this.getValue());
    }

}


package magick4j;

public class PercentValueHeightDistance extends ValueHeightDistance {

    public PercentValueHeightDistance(double value) {
	super(value);
    }

    public double getMagnitude(WidthDistance widthDistance, MagickImage image) {
	return widthDistance.getMagnitude(this, image);	
    }

    public double getMagnitude(RelativeWidthDistance widthDistance, MagickImage image) {
	return image.getHeight()*this.getValue()/100;
    }

    public double getMagnitude(PercentValueWidthDistance widthDistance, MagickImage image) {
	return image.getHeight()*this.getValue()/100;
    }

}

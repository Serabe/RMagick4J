package magick4j;

public class PercentValueWidthDistance extends ValueWidthDistance {

    public PercentValueWidthDistance(double value) {
	super(value);
    }

    public double getMagnitude(HeightDistance widthDistance, MagickImage image) {
	return widthDistance.getMagnitude(this, image);	
    }

    public double getMagnitude(RelativeHeightDistance widthDistance, MagickImage image) {
	return image.getWidth()*this.getValue()/100;
    }

    public double getMagnitude(PercentValueHeightDistance widthDistance, MagickImage image) {
	return image.getWidth()*this.getValue()/100;
    }

}

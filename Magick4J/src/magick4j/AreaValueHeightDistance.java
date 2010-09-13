package magick4j;

public class AreaValueHeightDistance extends ValueHeightDistance {

    public AreaValueHeightDistance(double value) {
	super(value);
    }

    public double getMagnitude(WidthDistance widthDistance, MagickImage image) {
	return widthDistance.getMagnitude(this, image);	
    }

    public double getMagnitude(RelativeWidthDistance widthDistance, MagickImage image) {
	return 0;
    }

    public double getMagnitude(AreaValueWidthDistance widthDistance, MagickImage image) {
	return Math.round(Math.sqrt(widthDistance.getValue()*image.getHeight()/image.getWidth()));
    }

}

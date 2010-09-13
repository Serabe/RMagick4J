package magick4j;

public class GreaterValueHeightDistance extends ValueHeightDistance {

    public GreaterValueHeightDistance(double value) {
	super(value);
    }

    public double getMagnitude(WidthDistance widthDistance, MagickImage image) {
	return widthDistance.getMagnitude(this, image);	
    }

    public double getMagnitude(RelativeWidthDistance widthDistance, MagickImage image) {
	if (image.getHeight() > this.getValue()) {
	    return new SimpleValueHeightDistance(this.getValue()).getMagnitude(widthDistance, image);	    
	} else {
	    return image.getHeight();
	}
    }

    public double getMagnitude(GreaterValueWidthDistance widthDistance, MagickImage image) {
	if (image.getHeight() > this.getValue() && image.getWidth() > widthDistance.getValue()) {
	    return new SimpleValueHeightDistance(this.getValue()).getMagnitude(new SimpleValueWidthDistance(widthDistance.getValue()), image);	    
	} else {
	    return image.getHeight();
	}
    }

}

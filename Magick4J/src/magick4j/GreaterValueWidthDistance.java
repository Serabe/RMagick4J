package magick4j;

public class GreaterValueWidthDistance extends ValueWidthDistance {

    public GreaterValueWidthDistance(double value) {
	super(value);
    }
        
    public double getMagnitude(HeightDistance heightDistance, MagickImage image) {
	return heightDistance.getMagnitude(this, image);	
    }

    public double getMagnitude(RelativeHeightDistance heightDistance, MagickImage image) {
	if (image.getWidth() > this.getValue()) {
	    return new SimpleValueWidthDistance(this.getValue()).getMagnitude(heightDistance, image);	    
	} else {
	    return image.getWidth();
	}
    }

    public double getMagnitude(GreaterValueHeightDistance heightDistance, MagickImage image) {
	if (image.getHeight() > heightDistance.getValue() && image.getWidth() > this.getValue()) {
	    return new SimpleValueWidthDistance(this.getValue()).getMagnitude(new SimpleValueHeightDistance(heightDistance.getValue()), image);	
	} else {
	    return image.getWidth();
	}
    }

}

package magick4j;

public class AspectValueWidthDistance extends ValueWidthDistance {

    public AspectValueWidthDistance(double value) {
	super(value);
    }
        
    public double getMagnitude(HeightDistance heightDistance, MagickImage image) {
	return heightDistance.getMagnitude(this, image);	
    }

    public double getMagnitude(RelativeHeightDistance heightDistance, MagickImage image) {
	return this.getValue();
    }

    public double getMagnitude(AspectValueHeightDistance heightDistance, MagickImage image) {
	return this.getValue();
    }

}

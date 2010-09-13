package magick4j;

public class SimpleValueWidthDistance extends ValueWidthDistance {

    public SimpleValueWidthDistance(double value) {
	super(value);
    }
        
    public double getMagnitude(HeightDistance heightDistance, MagickImage image) {
	return heightDistance.getMagnitude(this, image);	
    }

    public double getMagnitude(RelativeHeightDistance heightDistance, MagickImage image) {
	return this.getValue();
    }

    public double getMagnitude(SimpleValueHeightDistance heightDistance, MagickImage image) {
	return Math.min(Math.round(heightDistance.getValue()*image.getWidth()/image.getHeight()), this.getValue());
    }

}

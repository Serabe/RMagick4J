package magick4j;

public class AreaValueWidthDistance extends ValueWidthDistance {

    public AreaValueWidthDistance(double value) {
	super(value);
    }
        
    public double getMagnitude(HeightDistance heightDistance, MagickImage image) {
	return heightDistance.getMagnitude(this, image);	
    }

    public double getMagnitude(RelativeHeightDistance heightDistance, MagickImage image) {
	return Math.round(Math.sqrt(this.getValue()*image.getWidth()/image.getHeight()));
    }

    public double getMagnitude(AreaValueHeightDistance heightDistance, MagickImage image) {
	return Math.round(Math.sqrt(this.getValue()*image.getWidth()/image.getHeight()));
    }

}

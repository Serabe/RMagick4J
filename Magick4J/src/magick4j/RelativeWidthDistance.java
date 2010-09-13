package magick4j;

public class RelativeWidthDistance extends WidthDistance {

    public double getValue() {
	return 0;
    }

    public double getMagnitude(HeightDistance heightDistance, MagickImage image) {
	return heightDistance.getMagnitude(this, image);
    }
        
    public double getMagnitude(RelativeHeightDistance heigthDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

    public double getMagnitude(SimpleValueHeightDistance heightDistance, MagickImage image) {	
	return Math.round(heightDistance.getValue()*image.getWidth()/image.getHeight());
    }

    public double getMagnitude(PercentValueHeightDistance heightDistance, MagickImage image) {
	return Math.round(heightDistance.getMagnitude(this, image)*image.getWidth()/image.getHeight());
    }

    public double getMagnitude(LessValueHeightDistance heightDistance, MagickImage image) {
	if (image.getHeight() < heightDistance.getValue()) {
	    return this.getMagnitude(new SimpleValueHeightDistance(heightDistance.getValue()), image);
	} else {
	    return image.getWidth();
	}
    }

    public double getMagnitude(GreaterValueHeightDistance heightDistance, MagickImage image) {
	if (image.getHeight() > heightDistance.getValue()) {
	    return this.getMagnitude(new SimpleValueHeightDistance(heightDistance.getValue()), image);
	} else {
	    return image.getWidth();
	}
    }

    public double getMagnitude(AreaValueHeightDistance heightDistance, MagickImage image) {
	return 0;
    }

    public double getMagnitude(AspectValueHeightDistance heightDistance, MagickImage image) {
	return 0;
    }

}

package magick4j;

public class RelativeHeightDistance extends HeightDistance {

    public double getValue() {
	return 0;
    }

    public double getMagnitude(WidthDistance widthDistance, MagickImage image) {
	return widthDistance.getMagnitude(this, image);	
    }   
        
    public double getMagnitude(RelativeWidthDistance widthDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

    public double getMagnitude(SimpleValueWidthDistance widthDistance, MagickImage image) {	
	return Math.round(widthDistance.getValue()*image.getHeight()/image.getWidth());
    }

    public double getMagnitude(PercentValueWidthDistance widthDistance, MagickImage image) {
	return Math.round(widthDistance.getMagnitude(this, image)*image.getHeight()/image.getWidth());
    }

    public double getMagnitude(LessValueWidthDistance widthDistance, MagickImage image) {
	if (image.getWidth() < widthDistance.getValue()) {
	    return this.getMagnitude(new SimpleValueWidthDistance(widthDistance.getValue()), image);
	} else {
	    return image.getHeight();
	}
    }

    public double getMagnitude(GreaterValueWidthDistance widthDistance, MagickImage image) {
	if (image.getWidth() > widthDistance.getValue()) {
	    return this.getMagnitude(new SimpleValueWidthDistance(widthDistance.getValue()), image);
	} else {
	    return image.getHeight();
	}
    }

    public double getMagnitude(AreaValueWidthDistance widthDistance, MagickImage image) {
	return Math.round(Math.sqrt(widthDistance.getValue()*image.getHeight()/image.getWidth()));
    }

    public double getMagnitude(AspectValueWidthDistance widthDistance, MagickImage image) {
	return image.getHeight();
    }

}

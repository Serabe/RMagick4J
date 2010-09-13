package magick4j;

public abstract class ValueHeightDistance extends HeightDistance {

    private double value;

    public ValueHeightDistance(double value) {
	this.value = Math.abs(value);
    }

    public double getValue() {
	return this.value;
    }

    public double getMagnitude(SimpleValueWidthDistance widthDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

    public double getMagnitude(PercentValueWidthDistance widthDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

    public double getMagnitude(LessValueWidthDistance widthDistance, MagickImage image) {      
	throw new RuntimeException("incompatible dimension magnitudes");
    }

    public double getMagnitude(GreaterValueWidthDistance widthDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

    public double getMagnitude(AreaValueWidthDistance widthDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

    public double getMagnitude(AspectValueWidthDistance widthDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

}

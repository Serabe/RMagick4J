package magick4j;

public abstract class ValueWidthDistance extends WidthDistance {

    private double value;

    public ValueWidthDistance(double value) {
	this.value = Math.abs(value);
    }

    public double getValue() {
	return this.value;
    }

    public double getMagnitude(SimpleValueHeightDistance heightDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

    public double getMagnitude(PercentValueHeightDistance heightDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

    public double getMagnitude(LessValueHeightDistance heightDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

    public double getMagnitude(GreaterValueHeightDistance heightDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

    public double getMagnitude(AreaValueHeightDistance heightDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

    public double getMagnitude(AspectValueHeightDistance heightDistance, MagickImage image) {
	throw new RuntimeException("incompatible dimension magnitudes");
    }

}

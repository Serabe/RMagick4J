package magick4j;

public abstract class HeightDistance {

    public abstract double getValue();
    public abstract double getMagnitude(WidthDistance widthDistance, MagickImage image);
    public abstract double getMagnitude(RelativeWidthDistance widthDistance, MagickImage image);
    public abstract double getMagnitude(SimpleValueWidthDistance widthDistance, MagickImage image);
    public abstract double getMagnitude(PercentValueWidthDistance widthDistance, MagickImage image);
    public abstract double getMagnitude(LessValueWidthDistance widthDistance, MagickImage image);
    public abstract double getMagnitude(GreaterValueWidthDistance widthDistance, MagickImage image);
    public abstract double getMagnitude(AreaValueWidthDistance widthDistance, MagickImage image);
    public abstract double getMagnitude(AspectValueWidthDistance widthDistance, MagickImage image);

}

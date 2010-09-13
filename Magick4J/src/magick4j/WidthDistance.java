package magick4j;

public abstract class WidthDistance {
        
    public abstract double getValue();
    public abstract double getMagnitude(HeightDistance heightDistance, MagickImage image);
    public abstract double getMagnitude(RelativeHeightDistance heightDistance, MagickImage image);
    public abstract double getMagnitude(SimpleValueHeightDistance heightDistance, MagickImage image);
    public abstract double getMagnitude(PercentValueHeightDistance heightDistance, MagickImage image);
    public abstract double getMagnitude(LessValueHeightDistance heightDistance, MagickImage image);
    public abstract double getMagnitude(GreaterValueHeightDistance heightDistance, MagickImage image);
    public abstract double getMagnitude(AreaValueHeightDistance heightDistance, MagickImage image);
    public abstract double getMagnitude(AspectValueHeightDistance heightDistance, MagickImage image);

}

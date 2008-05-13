package magick4j;

public class PercentGeometry extends Geometry{

    public PercentGeometry(double width, double height){
        super(width, height);
    }
    
    public PercentGeometry(double width, double height, double x, double y){
        super(width, height, x, y);
    }
    
    @Override
    public double calculateHeight(MagickImage image){
        return image.getHeight()*this.getHeight()/100;
    }

    @Override
    public double calculateWidth(MagickImage image){
        return image.getWidth()*this.getWidth()/100;
    }
}

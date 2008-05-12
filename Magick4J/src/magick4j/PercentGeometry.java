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
        return new Geometry( image.getWidth()*this.getWidth()/100,
                             image.getHeight()*this.getHeight()/100,
                             this.getX(),
                             this.getY()).calculateHeight(image);
    }

    @Override
    public double calculateWidth(MagickImage image){
        return new Geometry( image.getWidth()*this.getWidth()/100,
                             image.getHeight()*this.getHeight()/100,
                             this.getX(),
                             this.getY()).calculateWidth(image);
    }
}

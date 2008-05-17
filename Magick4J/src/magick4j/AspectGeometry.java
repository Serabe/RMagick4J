
package magick4j;

public class AspectGeometry extends Geometry{

    public AspectGeometry(double width, double height){
        super(width, height);
    }
    
    public AspectGeometry(double width, double height, double x, double y){
        super(width, height, x, y);
    }
    
    @Override
    public double calculateHeight(MagickImage image){
        return this.getHeight();
    }

    @Override
    public double calculateWidth(MagickImage image){
        return this.getWidth();
    }
}


package magick4j;

public class AreaGeometry extends Geometry{

    public AreaGeometry(double width, double height){
        super(width, height);
    }
    
    public AreaGeometry(double width, double height, double x, double y){
        super(width, height, x, y);
    }
    
    @Override
    public double calculateHeight(MagickImage image){
        return Math.round(Math.sqrt(this.getWidth()*image.getHeight()/image.getWidth()));
    }
    
    @Override
    public double calculateWidth(MagickImage image){
        return Math.round(Math.sqrt(this.getWidth()*image.getWidth()/image.getHeight()));
    }

}

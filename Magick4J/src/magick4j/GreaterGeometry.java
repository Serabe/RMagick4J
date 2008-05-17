package magick4j;

public class GreaterGeometry extends Geometry{

    public GreaterGeometry(double width, double height){
        super(width, height);
    }
    
    public GreaterGeometry(double width, double height, double x, double y){
        super(width, height, x, y);
    }
    
    @Override
    public double calculateHeight(MagickImage image){
        if(image.getWidth() > this.getWidth() && image.getHeight() > this.getHeight()){
            return new Geometry(this.getWidth(),
                                this.getHeight(),
                                this.getX(),
                                this.getY()).calculateHeight(image);
        } else {
            return image.getHeight();
        }
    }

    @Override
    public double calculateWidth(MagickImage image){
        if(image.getWidth() > this.getWidth() && image.getHeight() > this.getHeight()){
            return new Geometry(this.getWidth(),
                                this.getHeight(),
                                this.getX(),
                                this.getY()).calculateWidth(image);
        } else {
            return image.getWidth();
        }
    }
}

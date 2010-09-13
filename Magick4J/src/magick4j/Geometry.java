package magick4j;

public class Geometry {
    
    private HeightDistance height;
    private WidthDistance width;
    private double x;
    private double y;

    public Geometry(WidthDistance width, HeightDistance height) {
        this(width, height, 0, 0);
    }
    
    public Geometry(WidthDistance width, HeightDistance height, double x, double y) {
        this.setWidth(width);
        this.setHeight(height);
        this.setX(x);
        this.setY(y);
    }       

    public double calculateHeight(MagickImage image){
        return width.getMagnitude(height, image);
    }

    public double calculateWidth(MagickImage image){
        return height.getMagnitude(width, image);
    }
    
    public double calculateX(MagickImage image){
        // TODO 
        return this.x;
    }
    
    public double calculateY(MagickImage image){
        return this.y;
    }

    public HeightDistance getHeight() {
        return this.height;
    }

    public WidthDistance getWidth() {
        return this.width;
    }
    
    public double getX(){
        return this.x;
    }
    
    public double getY(){
        return this.y;
    }    

    public void setHeight(HeightDistance height) {
	this.height = height;
    }

    public void setWidth(WidthDistance width) {
        this.width = width;
    }
    
    public void setX(double x){
        this.x = x;
    }
    
    public void setY(double y){
        this.y = y;
    }
}

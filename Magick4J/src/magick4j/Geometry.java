package magick4j;

public class Geometry {
    private double height=0;
    private double width=0;
    private double x=0;
    private double y=0;
    
    public Geometry(double width, double height){
        this(width, height, 0, 0);
    }
    
    public Geometry(double width, double height, double x, double y){
        this.setWidth(width);
        this.setHeight(height);
        this.setX(x);
        this.setY(y);
    }

    public double calculateHeight(MagickImage image){
        double newHeight = Math.round(this.getWidth()*image.getHeight()/image.getWidth());
        return Math.min(newHeight, this.getHeight());
    }

    public double calculateWidth(MagickImage image){
        double newWidth = Math.round(this.getHeight()*image.getWidth()/image.getHeight());
        return Math.min(newWidth, this.getWidth());
    }
    
    public double calculateX(MagickImage image){
        // TODO 
        return this.x;
    }
    
    public double calculateY(MagickImage image){
        return this.y;
    }

    public double getHeight(){
        return this.height;
    }

    public double getWidth(){
        return this.width;
    }
    
    public double getX(){
        return this.x;
    }
    
    public double getY(){
        return this.y;
    }

    public void setHeight(double height) {
        this.height = Math.abs(height);
    }

    public void setWidth(double width) {
        this.width = Math.abs(width);
    }
    
    public void setX(double x){
        this.x = x;
    }
    
    public void setY(double y){
        this.y = y;
    }
}

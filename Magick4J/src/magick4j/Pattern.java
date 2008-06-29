package magick4j;

import java.awt.image.BufferedImage;

public class Pattern {
    private BufferedImage image;
    private int x;
    private int y;
    private String name;
    
    public Pattern(String name, int x, int y, int width, int height){
        this.name = name;
        this.x = x;
        this.y = y;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }
    
    public BufferedImage getImage(){
        return this.image;
    }
    
    public String getName(){
        return this.name;
    }
    
    public int getX(){
        return this.x;
    }
    
    public int getY(){
        return this.y;
    }
}

package magick4j;

import java.awt.image.BufferedImage;

public class ClipPath {

    private MagickImage image;
    private String name;
    
    public ClipPath(String name, int width, int height){
        this.name = name;
        this.image = new MagickImage(width, height);
    }
    
    public MagickImage getImage(){
        return this.image;
    }
}

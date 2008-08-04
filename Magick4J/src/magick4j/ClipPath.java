package magick4j;

public class ClipPath {

    private MagickImage image;
    private String name;
    
    public ClipPath(String name, int width, int height){
//        ImageInfo info = new ImageInfo();
//        info.setBackgroundColor(new PixelPacket(255,255,255,0));
        this.name = name;
        this.image = new MagickImage(width, height/*, info*/);
    }
    
    public MagickImage getImage(){
        return this.image;
    }
}

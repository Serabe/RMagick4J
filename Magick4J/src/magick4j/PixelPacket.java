package magick4j;

import java.awt.Color;

/**
 * TODO Or is Color good enough? Do we support depth of 16 or 32??? Probably not
 * at first.
 */
public class PixelPacket {

    private int blue;
    private int green;
    private int opacity;
    private int red;

    public PixelPacket() {
        this(0, 0, 0, 0);
    }

    public PixelPacket(int red, int green, int blue) {
        this(red, green, blue, 0);
    }

    public PixelPacket(int red, int green, int blue, int opacity) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.opacity = opacity;
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof PixelPacket){
            PixelPacket pixel = (PixelPacket) o;
            return  this.getRed()     == pixel.getRed()   &&
                    this.getGreen()   == pixel.getGreen() &&
                    this.getBlue()    == pixel.getBlue()  &&
                    this.getOpacity() == pixel.getOpacity();
        }else{
            return true;
        }
    }

    public int getBlue() {
        return blue;
    }

    public int getGreen() {
        return green;
    }

    public int getOpacity() {
        return opacity;
    }

    public int getRed() {
        return red;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public Color toColor() {
        return new Color( red, green, blue, opacity);
    }
    
    @Override
    public String toString(){
        String name = ColorDatabase.getName(this);
        if(name == null){
            name = "rgb("+ this.getRed()   + ","+
                           this.getGreen() + ","+
                           this.getBlue()  + ")";
        }
        return name;
    }
}

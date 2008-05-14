package magick4j;

import java.awt.Color;

/**
 * TODO Or is Color good enough? Do we support depth of 16 or 32??? Probably not
 * at first.
 */
public class PixelPacket {

    private double blue;
    private double green;
    private double opacity;
    private double red;

    public PixelPacket() {
        this(0, 0, 0, 0);
    }

    public PixelPacket(double red, double green, double blue) {
        this(red, green, blue, 0);
    }

    public PixelPacket(double red, double green, double blue, double opacity) {
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

    public double getBlue() {
        return blue;
    }

    public double getGreen() {
        return green;
    }

    public double getOpacity() {
        return opacity;
    }

    public double getRed() {
        return red;
    }

    public void setBlue(double blue) {
        this.blue = blue;
    }

    public void setGreen(double green) {
        this.green = green;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public void setRed(double red) {
        this.red = red;
    }

    public Color toColor() {
        return new Color((float) red, (float) green, (float) blue, (float) opacity);
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

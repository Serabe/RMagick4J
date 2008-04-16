package magick4j;

public class ImageInfo {
    private PixelPacket backgroundColor;
    private Geometry size;

    public PixelPacket getBackgroundColor() {
        return backgroundColor;
    }

    public Geometry getSize() {
        return size;
    }

    public void setBackgroundColor(PixelPacket backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setSize(Geometry size) {
        this.size = size;
    }
}

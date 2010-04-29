package magick4j;

public class ImageInfo {
    private PixelPacket backgroundColor;
    private Geometry size;
    private String format;

    public PixelPacket getBackgroundColor() {
        return backgroundColor;
    }

    public String getFormat() {
        return this.format;
    }

    public Geometry getSize() {
        return size;
    }

    public void setBackgroundColor(PixelPacket backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setFormat(String format) {
    }

    public void setSize(Geometry size) {
        this.size = size;
    }
}

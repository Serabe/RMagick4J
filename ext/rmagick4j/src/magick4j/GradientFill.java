package magick4j;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;

public class GradientFill {
    private final PixelPacket endColor;
    private final PixelPacket startColor;
    private final double x1;
    private final double x2;
    private final double y1;
    private final double y2;

    public GradientFill(double x1, double y1, double x2, double y2, PixelPacket startColor, PixelPacket endColor) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public void fill(MagickImage image) {
        // I have to admit to looking at rmfill.c for some reference on how this works.
        Paint paint;
        if (Math.abs(x2 - x1) < 0.5) {
            if (Math.abs(y2 - y1) < 0.5) {
                // TODO point
                paint = null;
            } else {
                // vertical
                paint = new GradientPaint((float) x1, (float) 0, startColor.toColor(), image.getWidth(), 0, endColor.toColor());
            }
        } else if (Math.abs(y2 - y1) < 0.5) {
            // horizontal
            paint = new GradientPaint((float) 0, (float) y1, startColor.toColor(), 0, image.getHeight(), endColor.toColor());
        } else {
            // TODO diagonal
            paint = null;
        }
        
        Graphics2D graphics = image.getImage().createGraphics();
        try {
            graphics.setPaint(paint);
            graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        } finally {
            graphics.dispose();
        }
    }
    
}

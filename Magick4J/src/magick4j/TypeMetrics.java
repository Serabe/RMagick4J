package magick4j;

import java.awt.FontMetrics;

public class TypeMetrics {
    private double ascent = 0;
    private double descent = 0;
    private double height = 0;
    private double maxAdvance = 0;
    private double width = 0;

    public void compose(TypeMetrics metrics){
        
        if(metrics.getWidth() > getWidth()){
            this.setAscent( metrics.getAscent() );
            this.setDescent( metrics.getDescent() );
            this.setMaxAdvance( metrics.getMaxAdvance() );
            this.setWidth( metrics.getWidth() );
        }
        
        this.setHeight( this.getHeight() + metrics.getHeight() );
    }
    
    public static TypeMetrics fromFontMetrics(FontMetrics fontMetrics, String string){
        TypeMetrics metrics = new TypeMetrics();
        
        metrics.setAscent(fontMetrics.getAscent());
        metrics.setDescent(fontMetrics.getDescent());
        metrics.setHeight(fontMetrics.getHeight());
        metrics.setMaxAdvance(fontMetrics.getMaxAdvance());
        metrics.setWidth(fontMetrics.stringWidth(string));
        
        return metrics;
    }
    
    public double getAscent() {
        return ascent;
    }

    public double getDescent() {
        return descent;
    }

    public double getHeight() {
        return height;
    }

    public double getMaxAdvance() {
        return maxAdvance;
    }

    public double getWidth() {
        return width;
    }

    public void setAscent(double ascent) {
        this.ascent = ascent;
    }

    public void setDescent(double descent) {
        this.descent = descent;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setMaxAdvance(double maxAdvance) {
        this.maxAdvance = maxAdvance;
    }

    public void setWidth(double width) {
        this.width = width;
    }
}

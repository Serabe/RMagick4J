package magick4j;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Collection;

public abstract class CommandBuilder {
    
    public Command affine(final double sx, final double rx, final double ry, final double sy, final double tx, final double ty) {
        return new Command(){
            public void perform(DrawContext context){
                context.getInfo().getSpaceTransformation().concatenate(new AffineTransform(sx, rx, ry, sy, tx, ty));
            }
        };
    }
    
    public Command compose(final Collection<Command> commands){
        return new Command(){
            public void perform(DrawContext context){
                for(Command c : commands) c.perform(context);
            }
        };
    }
    
    public Command drawShape(final Color color, final Shape s){
        return new Command() {
            public void perform(DrawContext context){
                DrawInfo info = context.getInfo();
                
                // Calculate the new stroke width.
                double prevWidth = info.getStrokeWidth();
                double scaledXWidth = info.getSpaceTransformation().getScaleX() * prevWidth;
                double scaledYWidth = info.getSpaceTransformation().getScaleY() * prevWidth;
                double newWidth = Math.max(scaledXWidth, scaledYWidth);
                info.setStrokeWidth(newWidth);
                
                Shape shape = info.getSpaceTransformation().createTransformedShape(s);
              
                Graphics2D graphics = (Graphics2D) context.getGraphics().create();
                
                try {
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, info.isStrokeAntialias() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
                    
                    if (info.getStroke().toColor().getAlpha() > 0.0) {
                        double[] dashArray = info.getStrokeDashArray();
                        if (dashArray != null) {

                            float[] floatDashArray = new float[dashArray.length];
                            for (int d = 0; d < dashArray.length; d++) {
                                floatDashArray[d] = (float) dashArray[d];
                            }

                            graphics.setStroke(new BasicStroke((float) info.getStrokeWidth(), info.getStrokeLinecap(), info.getStrokeLinejoin(), info.getStrokeMiterLimit(), floatDashArray, 0f));

                        } else {
                            graphics.setStroke(new BasicStroke((float) info.getStrokeWidth(), info.getStrokeLinecap(), info.getStrokeLinejoin(), info.getStrokeMiterLimit()));
                        }
                        graphics.setColor(info.getStroke().toColor());
                        graphics.draw(shape);
                    }
                } finally {
                    graphics.dispose();
                
                    // Resets the width.
                    info.setStrokeWidth(prevWidth);
                }
            }
        };
    }
    
    public Command drawShapeWithPattern(final Pattern pattern, final Shape s){
        return new Command(){
            public void perform(DrawContext context){
                DrawInfo info = context.getInfo();
                
                // Calculate the new stroke width.
                double prevWidth = info.getStrokeWidth();
                double scaledXWidth = info.getSpaceTransformation().getScaleX() * prevWidth;
                double scaledYWidth = info.getSpaceTransformation().getScaleY() * prevWidth;
                double newWidth = Math.max(scaledXWidth, scaledYWidth);
                info.setStrokeWidth(newWidth);
                
                Shape shape = info.getSpaceTransformation().createTransformedShape(s);
              
                MagickImage canvas = new MagickImage(context.getImage().getWidth(), context.getImage().getHeight());
                
                canvas.setBackgroundColor(ColorDatabase.lookUp("white"));
                canvas.erase();
                
                Graphics2D graphics = (Graphics2D) canvas.getImage().createGraphics();
                
                try {
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, info.isStrokeAntialias() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
                    
                    double[] dashArray = info.getStrokeDashArray();
                    if (dashArray != null) {

                        float[] floatDashArray = new float[dashArray.length];
                        for (int d = 0; d < dashArray.length; d++) {
                            floatDashArray[d] = (float) dashArray[d];
                        }

                        graphics.setStroke(new BasicStroke((float) info.getStrokeWidth(), info.getStrokeLinecap(), info.getStrokeLinejoin(), info.getStrokeMiterLimit(), floatDashArray, 0f));

                    } else {
                        graphics.setStroke(new BasicStroke((float) info.getStrokeWidth(), info.getStrokeLinecap(), info.getStrokeLinejoin(), info.getStrokeMiterLimit()));
                    }
                    graphics.setColor(Color.BLACK);
                    graphics.draw(shape);
                } finally {
                    graphics.dispose();
                
                    // Resets the width.
                    info.setStrokeWidth(prevWidth);
                }
                
                context.getImage().mask(canvas, pattern);
            }
        };
    }
    
    public abstract Command fill(final String color);

    public abstract Command fillOpacity(final double opacity);
    
    public abstract Command fillRule(final int wind);
    
    public Command fillShape(final Color color, final Shape s){
        return new Command() {
            public void perform(DrawContext context){
                DrawInfo info = context.getInfo();
                
                Shape shape = info.getSpaceTransformation().createTransformedShape(s);
              
                Graphics2D graphics = (Graphics2D) context.getGraphics().create();
                
                try {
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, info.isStrokeAntialias() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
                    // TODO Should this be min, mult, or something else?
                    if (Math.min(info.getFillOpacity(), info.getFill().toColor().getAlpha()) > 0.0) {
                        Graphics2D fillGraphics = (Graphics2D) graphics.create();
                        try {
                            if (info.getFillOpacity() < 1.0) {
                                fillGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) info.getFillOpacity()));
                            }
                            
                            fillGraphics.setColor(info.getFill().toColor());
                            
                            fillGraphics.fill(shape);
                        } finally {
                            fillGraphics.dispose();
                        }
                    }
                } finally {
                    graphics.dispose();
                }
            }
        };
    }
    
    public Command fillShapeWithPattern(final Pattern pattern, final Shape s){
        return new Command() {
            public void perform(DrawContext context){
                DrawInfo info = context.getInfo();
                
                Shape shape = info.getSpaceTransformation().createTransformedShape(s);
                
                MagickImage canvas = new MagickImage(context.getImage().getWidth(), context.getImage().getHeight());
                
                canvas.setBackgroundColor(ColorDatabase.lookUp("white"));
                canvas.erase();
                
                Graphics2D graphics = (Graphics2D) canvas.getImage().createGraphics();
                
                try {
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, info.isStrokeAntialias() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
                    // TODO Should this be min, mult, or something else?
                    if (Math.min(info.getFillOpacity(), info.getFill().toColor().getAlpha()) > 0.0) {
                        Graphics2D fillGraphics = (Graphics2D) graphics.create();
                        try {
                            if (info.getFillOpacity() < 1.0) {
                                fillGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) info.getFillOpacity()));
                            }
                            
                            fillGraphics.setColor(Color.BLACK);
                            
                            fillGraphics.fill(shape);
                        } finally {
                            fillGraphics.dispose();
                        }
                    }
                } finally {
                    graphics.dispose();
                }
                
                context.getImage().mask(canvas, pattern);
            }
        };
    }
    
    public Command nil(){
        return new Command(){
            public void perform(DrawContext context){}
        };
    }

    public Command pop() {
        return new Command() {
            public void perform(DrawContext context) {
                context.pop();
            }
        };
    }

    public abstract Command prepareClipPath(String name);
    
    public Command push() {
        return new Command() {
            public void perform(DrawContext context) {
                context.push();
            }
        };
    }
    
    public abstract Command pushClipPath(final String name);

    public abstract Command pushPattern(final Pattern pattern);

    public Command rotate(final double rotation) {
        return new Command(){
            public void perform(DrawContext context){
                context.getInfo().rotate(rotation);
            }
        };
    }

    public Command scale(final double scaleX, final double scaleY) {
        return new Command() {
            public void perform(DrawContext context) {
                // TODO Change to a scale at the info level, so that push/pop works and so on.
                context.getInfo().scale(scaleX, scaleY);
            }
        };
    }
    
    public abstract Command shape(final Shape s);
   
    public Command skewX(final double degrees) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().skewX(degrees);
            }
        };
    }
    
    public Command skewY(final double degrees) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().skewY(degrees);
            }
        };
    }

    public abstract Command stroke(final Pattern pattern);

    public abstract Command stroke(final PixelPacket pixel);

    public abstract Command stroke(final String color);

    public abstract Command strokeAntialias(final boolean antialias);

    public abstract Command strokeDashArray(final double... lengths);

    public abstract Command strokeLinecap(final int linecap);

    public abstract Command strokeLinejoin(final int linejoin);

    public abstract Command strokeMiterLimit(final float miterLimit);

    public abstract Command strokeOpacity(final double opacity);

    public abstract Command strokeWidth(final double width);
    
    public Command translate(final double x, final double y) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().translate(x,y);
            }
        };
    }
}

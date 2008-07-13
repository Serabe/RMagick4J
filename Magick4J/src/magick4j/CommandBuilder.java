package magick4j;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Creates instances of the built-in drawing commands.
 */
public class CommandBuilder {

    private CommandBuilder() {
    // Hidden
    }

    public static Command affine(final double sx, final double rx, final double ry, final double sy, final double tx, final double ty) {
        return new Command(){
            public void perform(DrawContext context){
                context.getInfo().getSpaceTransformation().concatenate(new AffineTransform(sx, rx, ry, sy, tx, ty));
            }
        };
    }
    
    public static Command compose(final Collection<Command> commands){
        return new Command(){
            public void perform(DrawContext context){
                for(Command c : commands) c.perform(context);
            }
        };
    }
    
    public static Command drawShape(final Color color, final Shape s){
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
    
    public static Command drawShapeWithPattern(final Pattern pattern, final Shape s){
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
    
    public static Command fill(final String color) {
        return new Command() {
            public void perform(DrawContext context) {
                if(context.hasPattern(color)){
                    context.getInfo().setFill(context.getPattern(color));
                } else {
                    context.getInfo().setFill(ColorDatabase.queryDefault(color));
                }
            }
        };
    }

    public static Command fillOpacity(final double opacity) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setFillOpacity(opacity);
            }
        };
    }
    
    public static Command fillRule(final int wind) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setFillRule(wind);
            }
        };
    }
    
    public static Command fillShape(final Color color, final Shape s){
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
    
    public static Command fillShapeWithPattern(final Pattern pattern, final Shape s){
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
    
    public static Command nil(){
        return new Command(){
            public void perform(DrawContext context){}
        };
    }

    public static Command pop() {
        return new Command() {
            public void perform(DrawContext context) {
                context.pop();
            }
        };
    }

    public static Command push() {
        return new Command() {
            public void perform(DrawContext context) {
                context.push();
            }
        };
    }

    static Command pushPattern(final Pattern pattern) {
        return new Command(){
            public void perform(DrawContext context){
                context.addPattern(pattern);
                context.composePattern(pattern.getName());
            }
        };
    }

    public static Command scale(final double scaleX, final double scaleY) {
        return new Command() {
            public void perform(DrawContext context) {
                // TODO Change to a scale at the info level, so that push/pop works and so on.
                context.getGraphics().scale(scaleX, scaleY);
            }
        };
    }
    
    public static Command shape(final Shape s) {
        return new Command(){
            public void perform(DrawContext context){
                DrawInfo info = context.getInfo();
                List<Command> list = new ArrayList<Command>();
                if(s instanceof GeneralPath){
                    ((GeneralPath) s).setWindingRule(context.getInfo().getFillRule());
                }
                
                if(info.getFillPattern() == null){
                    // This is suposed to fix a bug in Java that do not "fill" a Line2D Shape.
                    if(s instanceof Line2D){
                        PixelPacket p = info.getStroke();
                        list.add(CommandBuilder.stroke(info.getFill()));
                        list.add(CommandBuilder.drawShape(info.getStroke().toColor(), s));
                        list.add(CommandBuilder.stroke(p));
                    } else {
                        list.add(CommandBuilder.fillShape(info.getFill().toColor(), s));
                    }
                } else {
                    // This is suposed to fix a bug in Java that do not "fill" a Line2D Shape.
                    if(s instanceof Line2D){
                        Pattern p = info.getStrokePattern();
                        list.add(CommandBuilder.stroke(info.getFillPattern()));
                        list.add(CommandBuilder.drawShapeWithPattern(info.getStrokePattern(), s));
                        list.add(CommandBuilder.stroke(p));
                    } else {
                        list.add(CommandBuilder.fillShapeWithPattern(info.getFillPattern(), s));
                    }
                }
                
                if(info.getStrokePattern() == null){
                    list.add(CommandBuilder.drawShape(info.getStroke().toColor(), s));
                } else {
                    list.add(CommandBuilder.drawShapeWithPattern(info.getStrokePattern(), s));
                }
                
                CommandBuilder.compose(list).perform(context);
            }
        };
    }
   
    
    public static Command skewX(final double degrees) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().skewX(degrees);
            }
        };
    }
    
    public static Command skewY(final double degrees) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().skewY(degrees);
            }
        };
    }

    private static Command stroke(final Pattern pattern) {
        return new Command() {
            public void perform(DrawContext context){
                context.getInfo().setStrokePattern(pattern);
            }
        };
    }

    public static Command stroke(final PixelPacket pixel) {
        return new Command() {
            public void perform(DrawContext context){
                context.getInfo().setStroke(pixel);
            }
        };
    }

    public static Command stroke(final String color){
        return new Command(){
            public void perform(DrawContext context){
                if(context.hasPattern(color)){
                    context.getInfo().setStroke(context.getPattern(color));
                } else {
                    context.getInfo().setStroke(ColorDatabase.queryDefault(color));
                }
            }

        };
    }

    public static Command strokeAntialias(final boolean antialias) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStrokeAntialias(antialias);
            }
        };
    }

    public static Command strokeDashArray(final double... lengths) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStrokeDashArray(lengths);
            }
        };
    }

    static Command strokeLinecap(final int linecap) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStrokeLinecap(linecap);
            }
        };
    }

    public static Command strokeLinejoin(final int linejoin) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStrokeLinejoin(linejoin);
            }
        };
    }

    public static Command strokeMiterLimit(final float miterLimit) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStrokeMiterLimit(miterLimit);
            }
        };
    }

    public static Command strokeOpacity(final double opacity) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().getStroke().setOpacity((int) Math.round(255*(1-opacity)));
            }
        };
    }

    public static Command strokeWidth(final double width) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStrokeWidth(width);
            }
        };
    }
}

package magick4j;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Collection;

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
    
    public static Command fill(final PixelPacket color) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setFill(color);
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

    public static Command shape(final Shape s) {
        return new Command() {
            public void perform(DrawContext context) {
                DrawInfo info = context.getInfo();
                
                // Calculate the new stroke width.
                double prevWidth = info.getStrokeWidth();
                double scaledXWidth = info.getSpaceTransformation().getScaleX() * prevWidth;
                double scaledYWidth = info.getSpaceTransformation().getScaleY() * prevWidth;
                double newWidth = Math.max(scaledXWidth, scaledYWidth);
                info.setStrokeWidth(newWidth);
                
                Graphics2D graphics = (Graphics2D) context.getGraphics().create();
                Shape shape = info.getSpaceTransformation().createTransformedShape(s);
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
                    // FIXME: null pattern stroke (or have correct default if there is one) [enebo]
                    // The default stoke now is black, as in RMagick. Waiting for the test to work to remove the fixme.[serabe]
                    // Waiting for a commit to have my comment in the repo, as the test works.
                    if (/*info.getStroke() != null &&*/ info.getStroke().toColor().getAlpha() > 0.0) {
                        double[] dashArray = info.getStrokeDashArray();
                        if (dashArray != null) {
                            
                            float[] floatDashArray = new float[dashArray.length];
                            for (int d = 0; d < dashArray.length; d++) {
                                floatDashArray[d] = (float) dashArray[d];
                            }
                            
                            graphics.setStroke(new BasicStroke((float) info.getStrokeWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, floatDashArray, 0f));
                            
                        } else {
                            graphics.setStroke(new BasicStroke((float) info.getStrokeWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                        }
                        graphics.setColor(info.getStroke().toColor());
                        graphics.draw(shape);
                    }
                } finally {
                    graphics.dispose();
                    
                    //Resets the width.
                    info.setStrokeWidth(prevWidth);
                }
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

    public static Command stroke(final PixelPacket color) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStroke(color);
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

    public static Command strokeOpacity(final double opacity) {
        return new Command() {
            public void perform(DrawContext context) {
            // TODO
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

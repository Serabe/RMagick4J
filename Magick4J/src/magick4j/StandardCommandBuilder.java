package magick4j;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates instances of the built-in drawing commands.
 */
public class StandardCommandBuilder extends CommandBuilder{

    final StandardCommandBuilder builder = this;
    
    @Override
    public Command fill(final String color) {
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

    @Override
    public Command fillOpacity(final double opacity) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setFillOpacity(opacity);
            }
        };
    }
    
    @Override
    public Command fillRule(final int wind) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setFillRule(wind);
            }
        };
    }
    
    @Override
    public Command prepareClipPath(final String name) {
        return new Command() {
            public void perform(DrawContext context) {
                context.prepareClipPath(name);
            }
        };
    }
    
    @Override
    public Command pushClipPath(final String name){
        return new Command() {
            public void perform(DrawContext context) {
                context.addClipPath(name);
                context.composeClipPath(name);
            }
        };
    }

    @Override
    public Command pushPattern(final Pattern pattern) {
        return new Command(){
            public void perform(DrawContext context){
                context.addPattern(pattern);
                context.composePattern(pattern.getName());
            }
        };
    }
    
    @Override
    public Command shape(final Shape s) {
        return new Command(){
            public void perform(DrawContext context){
                DrawInfo info = context.getInfo();
                List<Command> list = new ArrayList<Command>();
                if(s instanceof GeneralPath){
                    ((GeneralPath) s).setWindingRule(info.getFillRule());
                }

                if(info.getFillPattern() == null){
                    // This is suposed to fix a bug in Java that do not "fill" a Line2D Shape.
                    if(s instanceof Line2D){
                        PixelPacket p = info.getStroke();
                        list.add(builder.stroke(info.getFill()));
                        list.add(builder.drawShape(info.getStroke().toColor(), s));
                        list.add(builder.stroke(p));
                    } else {
                        list.add(builder.fillShape(info.getFill().toColor(), s));
                    }
                } else {
                    // This is suposed to fix a bug in Java that do not "fill" a Line2D Shape.
                    if(s instanceof Line2D){
                        Pattern p = info.getStrokePattern();
                        list.add(builder.stroke(info.getFillPattern()));
                        list.add(builder.drawShapeWithPattern(info.getStrokePattern(), s));
                        list.add(builder.stroke(p));
                    } else {
                        list.add(builder.fillShapeWithPattern(info.getFillPattern(), s));
                    }
                }

                if(info.getStrokePattern() == null){
                    list.add(builder.drawShape(info.getStroke().toColor(), s));
                } else {
                    list.add(builder.drawShapeWithPattern(info.getStrokePattern(), s));
                }

                builder.compose(list).perform(context);
            }
        };
    }

    @Override
    public Command stroke(final Pattern pattern) {
        return new Command() {
            public void perform(DrawContext context){
                context.getInfo().setStrokePattern(pattern);
            }
        };
    }

    @Override
    public Command stroke(final PixelPacket pixel) {
        return new Command() {
            public void perform(DrawContext context){
                context.getInfo().setStroke(pixel);
            }
        };
    }

    @Override
    public Command stroke(final String color){
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

    @Override
    public Command strokeAntialias(final boolean antialias) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStrokeAntialias(antialias);
            }
        };
    }

    @Override
    public Command strokeDashArray(final double... lengths) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStrokeDashArray(lengths);
            }
        };
    }

    @Override
    public Command strokeLinecap(final int linecap) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStrokeLinecap(linecap);
            }
        };
    }

    @Override
    public Command strokeLinejoin(final int linejoin) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStrokeLinejoin(linejoin);
            }
        };
    }

    @Override
    public Command strokeMiterLimit(final float miterLimit) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStrokeMiterLimit(miterLimit);
            }
        };
    }

    @Override
    public Command strokeOpacity(final double opacity) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().getStroke().setOpacity((int) Math.round(255*(1-opacity)));
            }
        };
    }

    @Override
    public Command strokeWidth(final double width) {
        return new Command() {
            public void perform(DrawContext context) {
                context.getInfo().setStrokeWidth(width);
            }
        };
    }
}

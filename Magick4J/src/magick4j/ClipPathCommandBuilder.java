package magick4j;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

public class ClipPathCommandBuilder extends CommandBuilder{

    private final GeneralPath path = new GeneralPath();
    
    public Command drawClipPath(){
        List<Command> list = new ArrayList<Command>();
        list.add(fillShape(Color.black, path));
        list.add(new Command(){
            public void perform(DrawContext context){
                context.pop();
            }
        });
        return compose(list);
    }
    
    @Override
    public Command fill(String color) {
        return nil();
    }

    @Override
    public Command fillOpacity(double opacity) {
        return nil();
    }

    @Override
    public Command fillRule(int wind) {
        //TODO Test if it works for the clip-path too or if its ignored.
        return nil();
    }
    
    @Override
    public Command pop(){
        return nil();
    }

    @Override
    public Command prepareClipPath(String name) {
        return nil();
    }
    
    @Override
    public Command pushClipPath(String name) {
        return nil();
    }

    @Override
    public Command pushPattern(Pattern pattern) {
        //TODO Test if it works for the clip-path too or if its ignored.
        return nil();
    }

    @Override
    public Command shape(final Shape s) {
        return new Command(){
            public void perform(DrawContext context){
                path.append(s, false);
            }
        };
    }

    @Override
    public Command stroke(Pattern pattern) {
        return nil();
    }

    @Override
    public Command stroke(PixelPacket pixel) {
        return nil();
    }

    @Override
    public Command stroke(String color) {
        return nil();
    }

    @Override
    public Command strokeAntialias(boolean antialias) {
        //TODO Test if it works for the clip-path too or if its ignored.
        return nil();
    }

    @Override
    public Command strokeDashArray(double... lengths) {
        //TODO Test if it works for the clip-path too or if its ignored.
        return nil();
    }

    @Override
    public Command strokeLinecap(int linecap) {
        //TODO Test if it works for the clip-path too or if its ignored.
        return nil();
    }

    @Override
    public Command strokeLinejoin(int linejoin) {
        //TODO Test if it works for the clip-path too or if its ignored.
        return nil();
    }

    @Override
    public Command strokeMiterLimit(float miterLimit) {
        //TODO Test if it works for the clip-path too or if its ignored.
        return nil();
    }

    @Override
    public Command strokeOpacity(double opacity) {
        return nil();
    }

    @Override
    public Command strokeWidth(double width) {
        //TODO Test if it works for the clip-path too or if its ignored.
        return nil();
    }
}

package magick4j;

import java.awt.BasicStroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * See http://studio.imagemagick.org/script/magick-vector-graphics.php for specs.
 */
public class CommandParser {
    
    private static final Map<String, ParserBuilder> PARSERS = buildBuilders();
    private static CommandBuilder currentBuilder = new StandardCommandBuilder();

    private static Map<String, ParserBuilder> buildBuilders() {
        Map<String, ParserBuilder> builders = new HashMap<String, ParserBuilder>();
        
        builders.put("affine", new ParserBuilder(){
            public Command build( String... parts){
                String[] args = parts[1].split(",");
                double sx = Double.parseDouble(args[0]);
                double rx = Double.parseDouble(args[1]);
                double ry = Double.parseDouble(args[2]);
                double sy = Double.parseDouble(args[3]);
                double tx = Double.parseDouble(args[4]);
                double ty = Double.parseDouble(args[5]);
                return CommandParser.getCurrentBuilder().affine(sx, rx, ry, sy, tx, ty);
            }
        });
        
        builders.put("arc", new ParserBuilder(){
            public Command build(String... parts){
                String[] originPoint = parts[1].split(",");
                String[] endPoint = parts[2].split(",");
                String[] degrees = parts[3].split(",");
                
                Point2D origin = new Point2D.Double(Double.parseDouble(originPoint[0]),Double.parseDouble(originPoint[1]));
                Point2D end = new Point2D.Double(Double.parseDouble(endPoint[0]),Double.parseDouble(endPoint[1]));
                double arcStart = Double.parseDouble(degrees[0]);
                double arcStop = Double.parseDouble(degrees[1]);
                
                return CommandParser.getCurrentBuilder().shape(new Arc2D.Double(origin.getX(), origin.getY(), end.getX()-origin.getX(), end.getY()-origin.getY(), -arcStart, -(arcStop - arcStart), Arc2D.OPEN));
            }
        });
        
        builders.put("bezier", new ParserBuilder(){
           public Command build(String... parts){
               List<Command> commands = new ArrayList<Command>();
               // TODO Study how bezier command work depending on the ammount
               // of points.
               return CommandParser.getCurrentBuilder().compose(commands);
           } 
        });
        
        builders.put("circle", new ParserBuilder() {
            public Command build(String... parts) {
                String[] args0 = parts[1].split(",");
                String[] args1 = parts[2].split(",");
                double centerX = Double.parseDouble(args0[0]);
                double centerY = Double.parseDouble(args0[1]);
                double perimeterX = Double.parseDouble(args1[0]);
                double perimeterY = Double.parseDouble(args1[1]);
                double dX = perimeterX - centerX;
                double dY = perimeterY - centerY;
                double radius = Math.sqrt(dX * dX + dY * dY);
                return CommandParser.getCurrentBuilder().shape(new Ellipse2D.Double(centerX - radius, centerY - radius, 2 * radius, 2 * radius));
            }
        });
        
        builders.put("clip-path", new ParserBuilder(){
            public Command build(String... parts){
                return CommandParser.getCurrentBuilder().prepareClipPath(parts[1]);
            }
        });
        
        builders.put("ellipse", new ParserBuilder() {
            public Command build(String... parts) {
                String[] args0 = parts[1].split(",");
                String[] args1 = parts[2].split(",");
                String[] args2 = parts[3].split(",");
                double centerX = Double.parseDouble(args0[0]);
                double centerY = Double.parseDouble(args0[1]);
                double radiusX = Double.parseDouble(args1[0]);
                double radiusY = Double.parseDouble(args1[1]);
                double arcStart = Double.parseDouble(args2[0]);
                double arcStop = Double.parseDouble(args2[1]);
                // TODO Custom primitive class to support OPEN strokes and PIE fills?
                return CommandParser.getCurrentBuilder().shape(new Arc2D.Double(centerX - radiusX, centerY - radiusY, 2 * radiusX, 2 * radiusY, -arcStart, -(arcStop - arcStart), Arc2D.OPEN));
            }
        });
        
        builders.put("fill", new ParserBuilder() {
            public Command build(String... parts) {
                String colorName = parts[1].replace("\"", "");
                return CommandParser.getCurrentBuilder().fill(colorName);
            }
        });
        
        builders.put("fill-opacity", new ParserBuilder() {
            public Command build(String... parts) {
                double opacity = Double.parseDouble(parts[1]);
                return CommandParser.getCurrentBuilder().fillOpacity(opacity);
            }
        });
        
        builders.put("fill-rule", new ParserBuilder() {
           public Command build(String... parts) {
               if(parts[1].equals("nonzero"))
                   return CommandParser.getCurrentBuilder().fillRule(GeneralPath.WIND_NON_ZERO);
               else
                   return CommandParser.getCurrentBuilder().fillRule(GeneralPath.WIND_EVEN_ODD);
           } 
        });
        
        builders.put("line", new ParserBuilder() {
            public Command build(String... parts) {
                String[] args0 = parts[1].split(",");
                String[] args1 = parts[2].split(",");
                double x1 = Double.parseDouble(args0[0]);
                double y1 = Double.parseDouble(args0[1]);
                double x2 = Double.parseDouble(args1[0]);
                double y2 = Double.parseDouble(args1[1]);
                // TODO Custom primitive to avoid fills?
                return CommandParser.getCurrentBuilder().shape(new Line2D.Double(x1, y1, x2, y2));
            }
        });
        
        builders.put("path", new PathParser());
        
        builders.put("polygon", new ParserBuilder() {
            public Command build(String... parts) {
                GeneralPath path = buildPolyline(parts);
                path.closePath();
                return CommandParser.getCurrentBuilder().shape(path);
            }
        });
        
        builders.put("polyline", new ParserBuilder() {
            public Command build(String... parts) {
                GeneralPath path = buildPolyline(parts);
                return CommandParser.getCurrentBuilder().shape(path);
            }
        });
        
        builders.put("pop", new ParserBuilder() {
            public Command build(String... parts) {
                String type = parts[1];
                
                if(type.equals("clip-path")){
                    Command c = ((ClipPathCommandBuilder) CommandParser.getCurrentBuilder()).drawClipPath();
                    CommandParser.setCurrentBuilder(new StandardCommandBuilder());
                    return c;
                }
                
                if(type.equals("defs")){
                    return CommandParser.getCurrentBuilder().nil(); // Yep, it does nothing.
                }
              
                if(type.equals("gradient")){
                    // TODO: Implement.
                    throw new RuntimeException("unknown pop type: gradient");
                }
              
                if(type.equals("graphic-context")) {
                    return CommandParser.getCurrentBuilder().pop();
                }
                
                if(type.equals("pattern")){
                    return CommandParser.getCurrentBuilder().nil();
                }
                
                throw new RuntimeException("unknown pop type: " + type);
            }
        });
        
        builders.put("push", new ParserBuilder() {
            public Command build(String... parts) {
                String type = parts[1];
                
                if(type.equals("clip-path")){
                    Command c = CommandParser.getCurrentBuilder().pushClipPath(parts[2]);
                    CommandParser.setCurrentBuilder(new ClipPathCommandBuilder());
                    return c;
                }
                
                if(type.equals("defs")){
                    return CommandParser.getCurrentBuilder().nil(); // Yep, it does nothing.
                }
              
                if(type.equals("gradient")){
                    // TODO: Implement.
                    throw new RuntimeException("unknown push type: gradient");
                }
              
                if(type.equals("graphic-context")) {
                    return CommandParser.getCurrentBuilder().push();
                }
                
                if(type.equals("pattern")){
                    String name = parts[2];
                    int x = Integer.parseInt(parts[3]);
                    int y = Integer.parseInt(parts[4]);
                    int width = Integer.parseInt(parts[5]);
                    int height = Integer.parseInt(parts[6]);
                    
                    Pattern pattern = new Pattern(name, x, y, width, height);
                    return CommandParser.getCurrentBuilder().pushPattern(pattern);
                }
                
                throw new RuntimeException("unknown push type: " + type);
            }
        });
        
        builders.put("rectangle", new ParserBuilder() {
            public Command build(String... parts) {
                String[] args0 = parts[1].split(",");
                String[] args1 = parts[2].split(",");
                double x1 = Double.parseDouble(args0[0]);
                double y1 = Double.parseDouble(args0[1]);
                double x2 = Double.parseDouble(args1[0]);
                double y2 = Double.parseDouble(args1[1]);
                return CommandParser.getCurrentBuilder().shape(new Rectangle2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1)));
            }
        });
        
        builders.put("rotate", new ParserBuilder() {
            public Command build(String... parts) {
                return CommandParser.getCurrentBuilder().rotate(Double.parseDouble(parts[1]));
            }
        });
        
        builders.put("roundrectangle", new ParserBuilder() {
            public Command build(String... parts) {
                String[] args = parts[1].split(",");
                double x1 = Double.parseDouble(args[0]);
                double y1 = Double.parseDouble(args[1]);
                double x2 = Double.parseDouble(args[2]);
                double y2 = Double.parseDouble(args[3]);
                double cornerWidth = Double.parseDouble(args[4]);
                double cornerHeight = Double.parseDouble(args[5]);
                return CommandParser.getCurrentBuilder().shape(new RoundRectangle2D.Double(x1, y1, x2 - x1, y2 - y1, cornerWidth, cornerHeight));
            }
        });
        
        builders.put("scale", new ParserBuilder() {
            public Command build(String... parts) {
                String[] args = parts[1].split(",");
                return CommandParser.getCurrentBuilder().scale(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
            }
        });
        
        builders.put("skewX", new ParserBuilder() {
           public Command build(String... parts) {
               return CommandParser.getCurrentBuilder().skewX(Double.parseDouble(parts[1]));
           } 
        });
        
        builders.put("skewY", new ParserBuilder() {
            public Command build(String... parts) {
                return CommandParser.getCurrentBuilder().skewY(Double.parseDouble(parts[1]));
            }
        });
        
        builders.put("stroke", new ParserBuilder() {
            public Command build(String... parts) {
                String colorName = parts[1].replace("\"", "");
                
                return CommandParser.getCurrentBuilder().stroke(colorName);
            }
        });
        
        builders.put("stroke-antialias", new ParserBuilder() {
            public Command build(String... parts) {
                return CommandParser.getCurrentBuilder().strokeAntialias(Integer.parseInt(parts[1]) == 1);
            }
        });
        
        builders.put("stroke-dasharray", new ParserBuilder() {
            public Command build(String... parts) {
                String[] args = parts[1].split(",");
                double[] lengths = new double[args.length];
                for (int a = 0; a < args.length; a++) {
                    lengths[a] = Double.parseDouble(args[a]);
                }
                return CommandParser.getCurrentBuilder().strokeDashArray(lengths);
            }
        });
        
        builders.put("stroke-linecap", new ParserBuilder(){
           public Command build(String... parts){
               int linecap=0;
               if("butt".equals(parts[1]))
                   linecap = BasicStroke.CAP_BUTT;
               else if("round".equals(parts[1]))
                   linecap = BasicStroke.CAP_ROUND;
               else //if("square".equals(parts[1]))
                   linecap = BasicStroke.CAP_SQUARE;
               
               return CommandParser.getCurrentBuilder().strokeLinecap(linecap);
           } 
        });
        
        builders.put("stroke-linejoin", new ParserBuilder() {
           public Command build(String... parts) {
               int linejoin = 0;
               if("miter".equals(parts[1]))
                   linejoin = BasicStroke.JOIN_MITER;
               else if("round".equals(parts[1]))
                   linejoin = BasicStroke.JOIN_ROUND;
               else //if("bevel".equals(parts[1]))
                   linejoin = BasicStroke.JOIN_BEVEL;
               
               return CommandParser.getCurrentBuilder().strokeLinejoin(linejoin);
           } 
        });
        
        builders.put("stroke-miterlimit", new ParserBuilder() {
           public Command build(String... parts) {
               return CommandParser.getCurrentBuilder().strokeMiterLimit(Float.parseFloat(parts[1]));
           }; 
        });
        
        builders.put("stroke-opacity", new ParserBuilder() {
            public Command build(String... parts) {
                // Manage two different type of string:
                // 1.- "0.3"
                // 2.- "30%"
                String opacity = parts[1];
                double value = 0.0;
                char[] string = opacity.toCharArray();
                if(string[string.length-1] == '%'){
                    opacity = opacity.substring(0, string.length-1);
                    value = Double.parseDouble(opacity)/100.0;
                }else{
                    value = Double.parseDouble(opacity);
                }
                return CommandParser.getCurrentBuilder().strokeOpacity(value);
            }
        });
        
        builders.put("stroke-width", new ParserBuilder() {
            public Command build(String... parts) {
                return CommandParser.getCurrentBuilder().strokeWidth(Double.parseDouble(parts[1]));
            }
        });
        
        builders.put("translate", new ParserBuilder() {
           public Command build(String... parts) {
               String[] point = parts[1].split(",");
               return CommandParser.getCurrentBuilder().translate(Double.parseDouble(point[0]), Double.parseDouble(point[1]));
           } 
        });
        
        return builders;
    }

    private static GeneralPath buildPolyline(String parts[]) {
        GeneralPath path = new GeneralPath();
        String[] args = parts[1].split(",");
        for (int a = 0; a < args.length; a += 2) {
            double x = Double.parseDouble(args[a]);
            double y = Double.parseDouble(args[a + 1]);
            if (a == 0) {
                path.moveTo((float) x, (float) y);
            } else {
                path.lineTo((float) x, (float) y);
            }
        }
        return path;
    }
    
    public static CommandBuilder getCurrentBuilder(){
        if(currentBuilder == null){
            System.out.println("CurrentBuilder es null *******************************************");
        }
        return currentBuilder;
    }

    public static List<Command> parse(String script) {
        try {
            List<Command> commands = new ArrayList<Command>();
            BufferedReader reader = new BufferedReader(new StringReader(script));
            // TODO MVG specs say this isn't really line based. They could all be on one line.
            String line;
            while ((line = reader.readLine()) != null){
                commands.add(parseCommand(line));
            }
            // Officially no need to close StringReaders.
            return commands;
        } catch (Exception e) {
            throw Thrower.throwAny(e);
        }
    }

    private static Command parseCommand(String text) {
        String[] parts = text.split(" +");
        String command = parts[0];
        ParserBuilder builder = PARSERS.get(command);
        if (builder == null) {
            // TODO This should also be the error for bad params (at least when I tested roundrectangle with only 2)
            // Magick::ImageMagickError: Non-conforming drawing primitive definition `yodle'
            // irb(main):018:0> Magick::ImageMagickError.superclass
            // => StandardError
            // TODO Also, this text might ought to be in the Ruby side, not in the Java side.
            throw new RuntimeException("Non-conforming drawing primitive definition `" + command + "'");
        }
        return builder.build(parts);
    }

    private static void setCurrentBuilder(CommandBuilder commandBuilder) {
        currentBuilder = commandBuilder;
    }
}

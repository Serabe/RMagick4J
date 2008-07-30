/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magick4j;

import com.kitfox.svg.pathcmd.Arc;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author serabe
 */
public class PathParser implements ParserBuilder{

    private Point2D currentPoint;
    private Point2D lastControlPointC = null;
    private Point2D lastControlPointQ = null;
    private GeneralPath path;
    private String[] params;
    
    private interface PathCommand{
        public void perform(PathParser parser);
    }
    
    private static final Map<String, PathCommand> COMMANDS = buildCommands();
    
    private static Map<String, PathCommand> buildCommands(){
        Map<String,PathCommand> commands = new HashMap<String,PathCommand>();
        
        commands.put("A", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 7; i+= 7){
                    double xRadius = Math.abs(Double.parseDouble(parser.getParams()[i]));
                    double yRadius = Math.abs(Double.parseDouble(parser.getParams()[i+1]));
                    double rotation = Double.parseDouble(parser.getParams()[i+2]);
                    boolean largeArcFlag = Double.parseDouble(parser.getParams()[i+3]) != 0;
                    boolean sweepFlag = Double.parseDouble(parser.getParams()[i+4]) != 0;
                    double x1 = parser.getCurrentPoint().getX();
                    double y1 = parser.getCurrentPoint().getY();
                    parser.getCurrentPoint().setLocation(   Double.parseDouble(parser.getParams()[i+5]),
                                                            Double.parseDouble(parser.getParams()[i+6]));

                    (new Arc()).arcTo(  parser.getPath(),
                                        (float) xRadius, (float) yRadius,
                                        (float) rotation,
                                        largeArcFlag, sweepFlag,
                                        (float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY(),
                                        (float) x1, (float) y1);
                }

                parser.setLastControlPointC(null);
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("a", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 7; i+= 7){
                    double xRadius = Math.abs(Double.parseDouble(parser.getParams()[i]));
                    double yRadius = Math.abs(Double.parseDouble(parser.getParams()[i+1]));
                    double rotation = Double.parseDouble(parser.getParams()[i+2]);
                    boolean largeArcFlag = Double.parseDouble(parser.getParams()[i+3]) != 0;
                    boolean sweepFlag = Double.parseDouble(parser.getParams()[i+4]) != 0;
                    double x1 = parser.getCurrentPoint().getX();
                    double y1 = parser.getCurrentPoint().getY();
                    parser.getCurrentPoint().setLocation(   parser.getCurrentPoint().getX() + Double.parseDouble(parser.getParams()[i+5]),
                                                            parser.getCurrentPoint().getY() + Double.parseDouble(parser.getParams()[i+6]));

                    (new Arc()).arcTo(  parser.getPath(),
                                        (float) xRadius, (float) yRadius,
                                        (float) rotation,
                                        largeArcFlag, sweepFlag,
                                        (float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY(),
                                        (float) x1, (float) y1);
                }

                parser.setLastControlPointC(null);
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("C", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 6; i+=6){

                    // First control point
                    float ctrlx1 = (float) (Double.parseDouble(parser.getParams()[i]));
                    float ctrly1 = (float) (Double.parseDouble(parser.getParams()[i+1]));

                    // Second control point
                    parser.setLastControlPointC(new Point2D.Double( Double.parseDouble(parser.getParams()[i+2]),
                                                                    Double.parseDouble(parser.getParams()[i+3])));
                    // End point becomes the current point.
                    parser.getCurrentPoint().setLocation(   Double.parseDouble(parser.getParams()[i+4]),
                                                            Double.parseDouble(parser.getParams()[i+5]));

                    parser.getPath().curveTo(   ctrlx1, ctrly1,
                                                (float) parser.getLastControlPointC().getX(), (float) parser.getLastControlPointC().getY(),
                                                (float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }

                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("c", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 6; i+=6){

                    // First control point
                    float ctrlx1 = (float) (parser.getCurrentPoint().getX() + Double.parseDouble(parser.getParams()[i]));
                    float ctrly1 = (float) (parser.getCurrentPoint().getY() + Double.parseDouble(parser.getParams()[i+1]));

                    // Second control point
                    parser.setLastControlPointC(new Point2D.Double( parser.getCurrentPoint().getX() + Double.parseDouble(parser.getParams()[i+2]),
                                                                    parser.getCurrentPoint().getY() + Double.parseDouble(parser.getParams()[i+3])));
                    // End point becomes the current point.
                    parser.getCurrentPoint().setLocation(   parser.getCurrentPoint().getX() + Double.parseDouble(parser.getParams()[i+4]),
                                                            parser.getCurrentPoint().getY() + Double.parseDouble(parser.getParams()[i+5]));

                    parser.getPath().curveTo(   ctrlx1, ctrly1,
                                    (float) parser.getLastControlPointC().getX(), (float) parser.getLastControlPointC().getY(),
                                    (float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }

                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("H", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i > 0; i++){
                    parser.getCurrentPoint().setLocation(Double.parseDouble(parser.getParams()[i]), parser.getCurrentPoint().getY());
                    parser.getPath().lineTo((float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }

                parser.setLastControlPointC(null);
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("h", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i > 0; i++){
                    parser.getCurrentPoint().setLocation(   parser.getCurrentPoint().getX() + Double.parseDouble(parser.getParams()[i]),
                                                            parser.getCurrentPoint().getY());
                    parser.getPath().lineTo((float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }

                parser.setLastControlPointC(null);
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("L", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 2; i+=2){
                    parser.getCurrentPoint().setLocation(Double.parseDouble(parser.getParams()[i]), Double.parseDouble(parser.getParams()[i+1]));
                    parser.getPath().lineTo((float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }

                parser.setLastControlPointC(null);
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("l", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 2; i+=2){
                    parser.getCurrentPoint().setLocation(  parser.getCurrentPoint().getX()+Double.parseDouble(parser.getParams()[i]),
                                                    parser.getCurrentPoint().getY()+Double.parseDouble(parser.getParams()[i+1]));
                    parser.getPath().lineTo((float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }

                parser.setLastControlPointC(null);
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("M", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 2; i+=2){
                    parser.getCurrentPoint().setLocation(Double.parseDouble(parser.getParams()[i]), Double.parseDouble(parser.getParams()[i+1]));
                    if(i==1){
                        parser.getPath().moveTo((float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                    } else {
                        parser.getPath().lineTo((float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                    }
                }

                parser.setLastControlPointC(null);
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("m", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 2; i+=2){
                    parser.getCurrentPoint().setLocation(   parser.getCurrentPoint().getX()+Double.parseDouble(parser.getParams()[i]),
                                                            parser.getCurrentPoint().getY()+Double.parseDouble(parser.getParams()[i+1]));
                    if(i==1){
                        parser.getPath().moveTo((float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                    } else {
                        parser.getPath().lineTo((float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                    }
                }

                parser.setLastControlPointC(null);
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("Q", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 4; i+=4){

                    // Control point.
                    parser.setLastControlPointQ(new Point2D.Double( Double.parseDouble(parser.getParams()[i]),
                                                                    Double.parseDouble(parser.getParams()[i+1])));

                    // End point.
                    parser.getCurrentPoint().setLocation(  Double.parseDouble(parser.getParams()[i+2]),
                                                    Double.parseDouble(parser.getParams()[i+3]));

                    parser.getPath().quadTo(    (float) parser.getLastControlPointQ().getX(), (float) parser.getLastControlPointQ().getY(),
                                                (float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }
                parser.setLastControlPointC(null);       
            }
        });
        
        commands.put("q", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 4; i+=4){

                    // Control point.
                    parser.setLastControlPointQ(new Point2D.Double( parser.getCurrentPoint().getX() + Double.parseDouble(parser.getParams()[i]),
                                                                    parser.getCurrentPoint().getY() + Double.parseDouble(parser.getParams()[i+1])));

                    // End point.
                    parser.getCurrentPoint().setLocation(   parser.getCurrentPoint().getX() + Double.parseDouble(parser.getParams()[i+2]),
                                                            parser.getCurrentPoint().getY() + Double.parseDouble(parser.getParams()[i+3]));

                    parser.getPath().quadTo((float) parser.getLastControlPointQ().getX(), (float) parser.getLastControlPointQ().getY(),
                                            (float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }
                parser.setLastControlPointC(null);
            }
        });
        
        commands.put("S", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 4; i+=4){

                    // First control point
                    if(parser.getLastControlPointC() == null){
                        parser.setLastControlPointC(parser.getCurrentPoint());
                    }
                    float ctrlx1 = (float)(2*parser.getCurrentPoint().getX() - parser.getLastControlPointC().getX());
                    float ctrly1 = (float)(2*parser.getCurrentPoint().getY() - parser.getLastControlPointC().getY());

                    // Second control 
                    parser.setLastControlPointC(new Point2D.Double( Double.parseDouble(parser.getParams()[i]),
                                                                    Double.parseDouble(parser.getParams()[i+1])));

                    // End point becomes the current point.
                    parser.getCurrentPoint().setLocation(Double.parseDouble(parser.getParams()[i+2]), Double.parseDouble(parser.getParams()[i+3]));

                    parser.getPath().curveTo(  ctrlx1, ctrly1,
                                        (float) parser.getLastControlPointC().getX(), (float) parser.getLastControlPointC().getY(),
                                        (float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }
                
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("s", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 4; i+=4){

                    // First control point
                    if(parser.getLastControlPointC() == null){
                        parser.setLastControlPointC(parser.getCurrentPoint());
                    }
                    float ctrlx1 = (float)(2*parser.getCurrentPoint().getX() - parser.getLastControlPointC().getX());
                    float ctrly1 = (float)(2*parser.getCurrentPoint().getY() - parser.getLastControlPointC().getY());

                    // Second control point
                    parser.setLastControlPointC(new Point2D.Double( parser.getCurrentPoint().getX() + Double.parseDouble(parser.getParams()[i]),
                                                                    parser.getCurrentPoint().getY() + Double.parseDouble(parser.getParams()[i+1])));

                    // End point becomes the current point.
                    parser.getCurrentPoint().setLocation(   parser.getCurrentPoint().getX() + Double.parseDouble(parser.getParams()[i+2]),
                                                            parser.getCurrentPoint().getY() + Double.parseDouble(parser.getParams()[i+3]));

                    parser.getPath().curveTo(   ctrlx1, ctrly1,
                                                (float) parser.getLastControlPointC().getX(), (float) parser.getLastControlPointC().getY(),
                                                (float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }
                
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("T", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 2; i+=2){

                    // First control point
                    if(parser.getLastControlPointQ() == null){
                        parser.setLastControlPointQ(parser.getCurrentPoint());
                    }
                    
                    parser.getLastControlPointQ().setLocation(  2*parser.getCurrentPoint().getX() - parser.getLastControlPointQ().getX(),
                                                                2*parser.getCurrentPoint().getY() - parser.getLastControlPointQ().getY());

                    // End point becomes the current point.
                    parser.getCurrentPoint().setLocation(Double.parseDouble(parser.getParams()[i]), Double.parseDouble(parser.getParams()[i+1]));

                    parser.getPath().quadTo(    (float) parser.getLastControlPointQ().getX(), (float) parser.getLastControlPointQ().getY(),
                                                (float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }
                
                parser.setLastControlPointC(null);
            }
        });
        
        commands.put("t", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i >= 2; i+=2){

                    // First control point
                    if(parser.getLastControlPointQ() == null){
                        parser.setLastControlPointQ(parser.getCurrentPoint());
                    }
                    
                    parser.getLastControlPointQ().setLocation(  2*parser.getCurrentPoint().getX() - parser.getLastControlPointQ().getX(),
                                                                2*parser.getCurrentPoint().getY() - parser.getLastControlPointQ().getY());

                    // End point becomes the current point.
                    parser.getCurrentPoint().setLocation(   parser.getCurrentPoint().getX() + Double.parseDouble(parser.getParams()[i]),
                                                            parser.getCurrentPoint().getY() + Double.parseDouble(parser.getParams()[i+1]));

                    parser.getPath().quadTo(    (float) parser.getLastControlPointQ().getX(), (float) parser.getLastControlPointQ().getY(),
                                                (float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }
                
                parser.setLastControlPointC(null);
            }
        });
        
        commands.put("V", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i > 0; i++){
                    parser.getCurrentPoint().setLocation(parser.getCurrentPoint().getX(), Double.parseDouble(parser.getParams()[i]));
                    parser.getPath().lineTo((float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }

                parser.setLastControlPointC(null);
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("v", new PathCommand(){
            public void perform(PathParser parser){
                for(int i = 1; parser.getParams().length - i > 0; i++){
                    parser.getCurrentPoint().setLocation(   parser.getCurrentPoint().getX(),
                                                            parser.getCurrentPoint().getY() + Double.parseDouble(parser.getParams()[i]));
                    parser.getPath().lineTo((float) parser.getCurrentPoint().getX(), (float) parser.getCurrentPoint().getY());
                }

                parser.setLastControlPointC(null);
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("Z", new PathCommand(){
            public void perform(PathParser parser){
                parser.getPath().closePath();
                parser.setLastControlPointC(null);
                parser.setLastControlPointQ(null);
            }
        });
        
        commands.put("z", new PathCommand(){
            public void perform(PathParser parser){
                parser.getPath().closePath();
                parser.setLastControlPointC(null);
                parser.setLastControlPointQ(null);
            }
        });
        
        return commands;
    }
    
    public Command build(String... parts) {
        String commandLine = "";

        // Join the parts.
        for(int i=1; i<parts.length; i++){
            commandLine += parts[i] + " ";
        }

        commandLine = commandLine.replaceAll("'", "");
        commandLine = commandLine.replaceAll(",", " "); // RMagick4J must be capable of recognize both comma separated values and blank space separated values.
        commandLine = commandLine.trim();

        String currentCommand = "";
        this.currentPoint = new Point2D.Double(0,0);
        this.path = new GeneralPath();
        this.path.setWindingRule(GeneralPath.WIND_EVEN_ODD);

        while(!commandLine.equals("")){

            // Get the current command.
            int i = 0;
            for(i=1; i<commandLine.length(); i++){
                if(Character.isLetter(commandLine.charAt(i))){
                    break;
                }
            }

            currentCommand = commandLine.substring(0, i);
            commandLine = commandLine.substring(i);

            if(currentCommand.length() > 1 && currentCommand.charAt(1) != ' '){
                currentCommand =    currentCommand.substring(0,1)+
                                    " "+
                                    currentCommand.substring(1);
            }
            
            this.setParams(currentCommand.split("[ ]+"));

            PathCommand command = COMMANDS.get(params[0]);
            if(command == null && !Character.isSpaceChar(params[0].charAt(0))){
                throw new RuntimeException("attribute not recognized: "+params[0].charAt(0));
            }else{
                command.perform(this);
            }

        }
        return CommandParser.getCurrentBuilder().shape(path);
    }
    
    public Point2D getCurrentPoint(){
        return this.currentPoint;
    }
    
    public Point2D getLastControlPointC(){
        return this.lastControlPointC;
    }
    
    public Point2D getLastControlPointQ(){
        return this.lastControlPointQ;
    }
    
    public String[] getParams(){
        return this.params;
    }
    
    public GeneralPath getPath(){
        return this.path;
    }
    
    public void setCurrentPoint(Point2D point){
        this.currentPoint = point;
    }
    
    public void setLastControlPointC(Point2D point){
        this.lastControlPointC = point;
    }
    
    public void setLastControlPointQ(Point2D point){
        this.lastControlPointQ = point;
    }
    
    public void setParams(String[] params){
        this.params = params;
    }
    
    public void setPath(GeneralPath path){
        this.path = path;
    }
}

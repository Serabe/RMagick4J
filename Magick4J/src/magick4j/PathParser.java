/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magick4j;

import com.kitfox.svg.pathcmd.Arc;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 *
 * @author serabe
 */
public class PathParser implements ParserBuilder{

    private Point2D lastControlPointC = null;
    private Point2D lastControlPointQ = null;
    private Point2D currentPoint;
    GeneralPath path;
    
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

            String[] params = currentCommand.split(" ");



            switch(params[0].charAt(0)){
                case 'A':
                    this.arcAbsoluteCommand(params);
                    break;

                case 'a':
                    this.arcRelativeCommand(params);
                    break;

                case 'C':
                    this.cubicBezierAbsoluteCommand(params);
                    break;

                case 'c':
                    this.cubicBezierRelativeCommand(params);
                    break;

                case 'H':
                    this.horizontalLineAbsoluteCommand(params);
                    break;

                case 'h':
                    this.horizontalLineRelativeCommand(params);
                    break;

                case 'L':
                    this.lineAbsoluteCommand(params);
                    break;

                case 'l':
                    this.lineRelativeCommand(params);
                    break;

                case 'M':
                    this.moveToAbsoluteCommand(params);
                    break;

                case 'm':
                    this.moveToRelativeCommand(params);
                    break;

                case 'Q':
                    this.quadraticBezierAbsoluteCommand(params);
                    break;

                case 'q':
                    this.quadraticBezierRelativeCommand(params);
                    break;

                case 'S':
                    this.shorthandCubicBezierAbsoluteCommand(params);
                    break;

                case 's':
                    this.shorthandCubicBezierRelativeCommand(params);
                    break;

                case 'T':
                    this.shorthandQuadraticBezierAbsoluteCommand(params);
                    break;

                case 't':
                    this.shorthandQuadraticBezierRelativeCommand(params);
                    break;

                case 'V':
                    this.verticalLineAbsoluteCommand(params);
                    break;

                case 'v':
                    this.verticalLineRelativeCommand(params);
                    break;

                case 'Z':
                case 'z':
                    this.closePath(params);
                    break;

                default:
                    if(!Character.isSpaceChar(params[0].charAt(0)))
                        throw new RuntimeException("attribute not recognized: "+params[0].charAt(0));
            }

        }
        return CommandBuilder.shape(path);
    }

    private void arcAbsoluteCommand(String[] params) {
        for(int i = 1; params.length - i >= 7; i+= 7){
            double xRadius = Math.abs(Double.parseDouble(params[i]));
            double yRadius = Math.abs(Double.parseDouble(params[i+1]));
            double rotation = Double.parseDouble(params[i+2]);
            boolean largeArcFlag = Double.parseDouble(params[i+3]) != 0;
            boolean sweepFlag = Double.parseDouble(params[i+4]) != 0;
            double x1 = this.currentPoint.getX();
            double y1 = this.currentPoint.getY();
            this.currentPoint.setLocation(  Double.parseDouble(params[i+5]),
                                            Double.parseDouble(params[i+6]));

            (new Arc()).arcTo(  this.path,
                                (float) xRadius, (float) yRadius,
                                (float) rotation,
                                largeArcFlag, sweepFlag,
                                (float) this.currentPoint.getX(), (float) this.currentPoint.getY(),
                                (float) x1, (float) y1);
        }
        
        this.lastControlPointC = null;
        this.lastControlPointQ = null;            
    }
    
    private void arcRelativeCommand(String[] params){
        for(int i = 1; params.length - i >= 7; i+= 7){
            double xRadius = Math.abs(Double.parseDouble(params[i]));
            double yRadius = Math.abs(Double.parseDouble(params[i+1]));
            double rotation = Double.parseDouble(params[i+2]);
            boolean largeArcFlag = Double.parseDouble(params[i+3]) != 0;
            boolean sweepFlag = Double.parseDouble(params[i+4]) != 0;
            double x1 = this.currentPoint.getX();
            double y1 = this.currentPoint.getY();
            this.currentPoint.setLocation(  this.currentPoint.getX() + Double.parseDouble(params[i+5]),
                                            this.currentPoint.getY() + Double.parseDouble(params[i+6]));

            (new Arc()).arcTo(  this.path,
                                (float) xRadius, (float) yRadius,
                                (float) rotation,
                                largeArcFlag, sweepFlag,
                                (float) this.currentPoint.getX(), (float) this.currentPoint.getY(),
                                (float) x1, (float) y1);
        }
        
        this.lastControlPointC = null;
        this.lastControlPointQ = null;
                    
    }

    private void closePath(String[] params) {
        this.path.closePath();
        this.lastControlPointC = null;
        this.lastControlPointQ = null;
    }

    private void cubicBezierAbsoluteCommand(String[] params) {
        for(int i = 1; params.length - i >= 6; i+=6){

            // First control point
            float ctrlx1 = (float) (Double.parseDouble(params[i]));
            float ctrly1 = (float) (Double.parseDouble(params[i+1]));

            // Second control point
            this.lastControlPointC = new Point2D.Double(    Double.parseDouble(params[i+2]),
                                                            Double.parseDouble(params[i+3]));
            // End point becomes the current point.
            this.currentPoint.setLocation(  Double.parseDouble(params[i+4]),
                                            Double.parseDouble(params[i+5]));

            this.path.curveTo(  ctrlx1, ctrly1,
                                (float) this.lastControlPointC.getX(), (float) this.lastControlPointC.getY(),
                                (float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }
        
        this.lastControlPointQ = null;         
    }
    
    private void cubicBezierRelativeCommand(String[] params){
        for(int i = 1; params.length - i >= 6; i+=6){

            // First control point
            float ctrlx1 = (float) (this.currentPoint.getX() + Double.parseDouble(params[i]));
            float ctrly1 = (float) (this.currentPoint.getY() + Double.parseDouble(params[i+1]));

            // Second control point
            this.lastControlPointC = new Point2D.Double(    this.currentPoint.getX() + Double.parseDouble(params[i+2]),
                                                            this.currentPoint.getY() + Double.parseDouble(params[i+3]));
            // End point becomes the current point.
            this.currentPoint.setLocation(  this.currentPoint.getX() + Double.parseDouble(params[i+4]),
                                            this.currentPoint.getY() + Double.parseDouble(params[i+5]));

            this.path.curveTo(   ctrlx1, ctrly1,
                            (float) this.lastControlPointC.getX(), (float) this.lastControlPointC.getY(),
                            (float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }
        
        this.lastControlPointQ = null;
                    
    }

    private void horizontalLineAbsoluteCommand(String[] params) {
        for(int i = 1; params.length - i > 0; i++){
            this.currentPoint.setLocation(Double.parseDouble(params[i]), this.currentPoint.getY());
            this.path.lineTo((float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }
        
        this.lastControlPointC = null;
        this.lastControlPointQ = null;
                    
    }

    private void horizontalLineRelativeCommand(String[] params) {
        for(int i = 1; params.length - i > 0; i++){
            this.currentPoint.setLocation(  this.currentPoint.getX() + Double.parseDouble(params[i]),
                                            this.currentPoint.getY());
            this.path.lineTo((float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }
        
        this.lastControlPointC = null;
        this.lastControlPointQ = null;
        
    }

    private void lineAbsoluteCommand(String[] params){
        for(int i = 1; params.length - i >= 2; i+=2){
            this.currentPoint.setLocation(Double.parseDouble(params[i]), Double.parseDouble(params[i+1]));
            this.path.lineTo((float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }
        
        this.lastControlPointC = null;
        this.lastControlPointQ = null;
        
    }

    private void lineRelativeCommand(String[] params) {
        for(int i = 1; params.length - i >= 2; i+=2){
            this.currentPoint.setLocation(  this.currentPoint.getX()+Double.parseDouble(params[i]),
                                            this.currentPoint.getY()+Double.parseDouble(params[i+1]));
            this.path.lineTo((float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }
        
        this.lastControlPointC = null;
        this.lastControlPointQ = null;
        
    }

    private void moveToAbsoluteCommand(String[] params) {
        for(int i = 1; params.length - i >= 2; i+=2){
            this.currentPoint.setLocation(Double.parseDouble(params[i]), Double.parseDouble(params[i+1]));
            if(i==1){
                this.path.moveTo((float) this.currentPoint.getX(), (float) this.currentPoint.getY());
            } else {
                this.path.lineTo((float) this.currentPoint.getX(), (float) this.currentPoint.getY());
            }
        }
        
        this.lastControlPointC = null;
        this.lastControlPointQ = null;
        
    }

    private void moveToRelativeCommand(String[] params) {
        for(int i = 1; params.length - i >= 2; i+=2){
            this.currentPoint.setLocation(  this.currentPoint.getX()+Double.parseDouble(params[i]),
                                            this.currentPoint.getY()+Double.parseDouble(params[i+1]));
            if(i==1){
                this.path.moveTo((float) this.currentPoint.getX(), (float) this.currentPoint.getY());
            } else {
                this.path.lineTo((float) this.currentPoint.getX(), (float) this.currentPoint.getY());
            }
        }
        
        this.lastControlPointC = null;
        this.lastControlPointQ = null;
        
    }

    private void quadraticBezierAbsoluteCommand(String[] params) {
        for(int i = 1; params.length - i >= 4; i+=4){

            // Control point.
            this.lastControlPointQ = new Point2D.Double(    Double.parseDouble(params[i]),
                                                            Double.parseDouble(params[i+1]));

            // End point.
            this.currentPoint.setLocation(  Double.parseDouble(params[i+2]),
                                            Double.parseDouble(params[i+3]));

            this.path.quadTo(   (float) this.lastControlPointQ.getX(), (float) this.lastControlPointQ.getY(),
                                (float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }
        this.lastControlPointC=null;          
    }

    private void quadraticBezierRelativeCommand(String[] params) {
        for(int i = 1; params.length - i >= 4; i+=4){

            // Control point.
            this.lastControlPointQ = new Point2D.Double(    this.currentPoint.getX() + Double.parseDouble(params[i]),
                                                            this.currentPoint.getY() + Double.parseDouble(params[i+1]));

            // End point.
            this.currentPoint.setLocation(   this.currentPoint.getX() + Double.parseDouble(params[i+2]),
                                        this.currentPoint.getY() + Double.parseDouble(params[i+3]));

            this.path.quadTo(   (float) this.lastControlPointQ.getX(), (float) this.lastControlPointQ.getY(),
                                (float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }
        this.lastControlPointC=null;
    }

    private void shorthandCubicBezierAbsoluteCommand(String[] params) {
        for(int i = 1; params.length - i >= 4; i+=4){

            // First control point
            if(this.lastControlPointC == null){
                this.lastControlPointC = this.currentPoint;
            }
            float ctrlx1 = (float)(2*this.currentPoint.getX() - this.lastControlPointC.getX());
            float ctrly1 = (float)(2*this.currentPoint.getY() - this.lastControlPointC.getY());

            // Second control 
            this.lastControlPointC = new Point2D.Double( Double.parseDouble(params[i]),
                                                    Double.parseDouble(params[i+1]));

            // End point becomes the current point.
            this.currentPoint.setLocation(Double.parseDouble(params[i+2]), Double.parseDouble(params[i+3]));

            this.path.curveTo(  ctrlx1, ctrly1,
                                (float) this.lastControlPointC.getX(), (float) this.lastControlPointC.getY(),
                                (float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }
        this.lastControlPointQ = null;
    }

    private void shorthandCubicBezierRelativeCommand(String[] params) {
        for(int i = 1; params.length - i >= 4; i+=4){

            // First control point
            if(this.lastControlPointC == null){
                this.lastControlPointC = this.currentPoint;
            }
            float ctrlx1 = (float)(2*this.currentPoint.getX() - this.lastControlPointC.getX());
            float ctrly1 = (float)(2*this.currentPoint.getY() - this.lastControlPointC.getY());

            // Second control point
            this.lastControlPointC = new Point2D.Double( this.currentPoint.getX() + Double.parseDouble(params[i]),
                                                    this.currentPoint.getY() + Double.parseDouble(params[i+1]));

            // End point becomes the current point.
            this.currentPoint.setLocation(  this.currentPoint.getX() + Double.parseDouble(params[i+2]),
                                            this.currentPoint.getY() + Double.parseDouble(params[i+3]));

            this.path.curveTo(   ctrlx1, ctrly1,
                            (float) this.lastControlPointC.getX(), (float) this.lastControlPointC.getY(),
                            (float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }
        this.lastControlPointQ = null;
    }

    private void shorthandQuadraticBezierAbsoluteCommand(String[] params) {
        for(int i = 1; params.length - i >= 2; i+=2){

            // First control point
            if(this.lastControlPointQ == null){
                this.lastControlPointQ = this.currentPoint;
            }
            this.lastControlPointQ.setLocation( 2*this.currentPoint.getX() - this.lastControlPointQ.getX(),
                                                2*this.currentPoint.getY() - this.lastControlPointQ.getY());

            // End point becomes the current point.
            this.currentPoint.setLocation(Double.parseDouble(params[i]), Double.parseDouble(params[i+1]));

            this.path.quadTo(   (float) this.lastControlPointQ.getX(), (float) this.lastControlPointQ.getY(),
                                (float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }
        this.lastControlPointC = null;
    }

    private void shorthandQuadraticBezierRelativeCommand(String[] params) {
        for(int i = 1; params.length - i >= 2; i+=2){

            // First control point
            if(this.lastControlPointQ == null){
                this.lastControlPointQ = this.currentPoint;
            }
            this.lastControlPointQ.setLocation( 2*this.currentPoint.getX() - this.lastControlPointQ.getX(),
                                                2*this.currentPoint.getY() - this.lastControlPointQ.getY());

            // End point becomes the current point.
            this.currentPoint.setLocation(  this.currentPoint.getX() + Double.parseDouble(params[i]),
                                            this.currentPoint.getY() + Double.parseDouble(params[i+1]));

            this.path.quadTo(   (float) this.lastControlPointQ.getX(), (float) this.lastControlPointQ.getY(),
                                (float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }
        this.lastControlPointC = null;
    }

    private void verticalLineAbsoluteCommand(String[] params) {
        for(int i = 1; params.length - i > 0; i++){
            this.currentPoint = new Point2D.Double(this.currentPoint.getX(), Double.parseDouble(params[i]));
            this.path.lineTo((float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }

        this.lastControlPointC = null;
        this.lastControlPointQ = null;
    }

    private void verticalLineRelativeCommand(String[] params) {
        for(int i = 1; params.length - i > 0; i++){
            this.currentPoint = new Point2D.Double( this.currentPoint.getX(),
                                                    this.currentPoint.getY() + Double.parseDouble(params[i]));
            this.path.lineTo((float) this.currentPoint.getX(), (float) this.currentPoint.getY());
        }

        this.lastControlPointC = null;
        this.lastControlPointQ = null;
    }
}

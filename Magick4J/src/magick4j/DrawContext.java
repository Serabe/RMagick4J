package magick4j;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DrawContext {
    private String composingPattern = null;
    private List<Graphics2D> graphicsStack = new ArrayList<Graphics2D>();
    private MagickImage image;
    private List<DrawInfo> infoStack = new ArrayList<DrawInfo>();
    private Hashtable<String, Pattern> patternHash = new Hashtable<String, Pattern>();
    

    public DrawContext(DrawInfo info, MagickImage image) {
        this.graphicsStack.add((Graphics2D) image.getImage().createGraphics());
        this.infoStack.add(info);
        this.image = image;
    }

    void addPattern(Pattern pattern) {
        this.patternHash.put(pattern.getName(), pattern);
    }

    void composePattern(String name) {
        this.composingPattern = name;
    }

    /**
     * Dispose all but the original graphics, assuming any remain.
     * This context is unusable after disposal.
     */
    public void dispose() {
        while (graphicsStack.size() > 1) {
            try {
                pop();
            } catch (Exception e) {
                // Plow on, but log first.
                Logger.getLogger(DrawContext.class.getName()).log(Level.WARNING, "error disposing images " + graphicsStack.size(), e);
            }
        }
        graphicsStack = null;
        infoStack = null;
    }

    public Graphics2D getGraphics() {
        return (Graphics2D) graphicsStack.get(graphicsStack.size() - 1);
    }

    public MagickImage getImage(){
        return this.image;
    }
    
    public DrawInfo getInfo() {
        return infoStack.get(infoStack.size() - 1);
    }
    
    public Pattern getPattern(String name){
        return this.patternHash.get(name);
    }
    
    public boolean hasPattern(String name){
        return this.patternHash.containsKey(name);
    }

    public void pop() {
        try {
            getGraphics().dispose();
        } finally {
            graphicsStack.remove(graphicsStack.size() - 1);
            infoStack.remove(infoStack.size() - 1);
        }
    }

    public void push() {
        if(this.composingPattern != null){
            graphicsStack.add((Graphics2D) this.patternHash.get(this.composingPattern).getImage().getGraphics());
            this.composingPattern = null;
        }else{
            graphicsStack.add((Graphics2D) getGraphics().create());
        }
        infoStack.add(getInfo().clone());
    }
}

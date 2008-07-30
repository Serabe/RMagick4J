package magick4j;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DrawContext {
    private String applyClipPath = null;
    private Hashtable<String, ClipPath> clipPathHash = new Hashtable<String, ClipPath>();
    private String composingClipPath = null;
    private String composingPattern = null;
    private List<MagickImage> imagesStack = new ArrayList<MagickImage>();
    private List<DrawInfo> infoStack = new ArrayList<DrawInfo>();
    private Hashtable<String, Pattern> patternHash = new Hashtable<String, Pattern>();
    

    public DrawContext(DrawInfo info, MagickImage image) {
        this.imagesStack.add(image);
        this.infoStack.add(info);
    }

    void addClipPath(String name) {
        this.clipPathHash.put(name, new ClipPath(name, getImage().getWidth(), getImage().getHeight()));
    }

    void addPattern(Pattern pattern) {
        this.patternHash.put(pattern.getName(), pattern);
    }

    void composeClipPath(String name) {
        this.composingClipPath = name;
    }

    void composePattern(String name) {
        this.composingPattern = name;
    }

    /**
     * Dispose all but the original graphics, assuming any remain.
     * This context is unusable after disposal.
     */
    public void dispose() {
        while (imagesStack.size() > 1) {
            try {
                pop();
            } catch (Exception e) {
                // Plow on, but log first.
                Logger.getLogger(DrawContext.class.getName()).log(Level.WARNING, "error disposing images " + imagesStack.size(), e);
            }
        }
        imagesStack = null;
        infoStack = null;
    }

    public ClipPath getClipPath(String name){
        return this.clipPathHash.get(name);
    }
    
    public Graphics2D getGraphics() {
        return (Graphics2D) getImage().getImage().getGraphics();
    }

    public MagickImage getImage(){
        return this.imagesStack.get(this.imagesStack.size()-1);
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
            if(this.applyClipPath != null){
                imagesStack.get(imagesStack.size() - 1).applyMask(this.clipPathHash.get(this.applyClipPath).getImage());
                this.applyClipPath = null;
            }
            if(this.composingClipPath == null && this.composingPattern == null){
                imagesStack.get(imagesStack.size() - 2).composite(imagesStack.get(imagesStack.size() - 1), 0, 0, CompositeOperator.OVER);
            }else{
                this.composingClipPath = null;
                this.composingPattern = null;
            }
        } finally {
            imagesStack.remove(imagesStack.size() - 1);
            infoStack.remove(infoStack.size() - 1);
        }
    }

    public void prepareClipPath(String name) {
        this.applyClipPath = name;
    }

    public void push() {
        if(this.composingPattern != null){
            imagesStack.add(this.patternHash.get(this.composingPattern).getImage());
        } else if(this.composingClipPath != null){
            imagesStack.add(this.clipPathHash.get(this.composingClipPath).getImage());
        } else{
            imagesStack.add(getImage().createCanvas());
        }
        infoStack.add(getInfo().clone());
    }
}

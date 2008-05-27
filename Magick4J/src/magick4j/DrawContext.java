package magick4j;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DrawContext {
    private List<Graphics2D> graphicsStack = new ArrayList<Graphics2D>();
    private List<DrawInfo> infoStack = new ArrayList<DrawInfo>();
    private SpaceTransformation spaceTransformation = new SpaceTransformation();

    public DrawContext(DrawInfo info, Graphics2D graphics) {
        graphicsStack.add(graphics);
        infoStack.add(info);
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
                Logger.getLogger(DrawContext.class.getName()).log(Level.WARNING, "error disposing graphics " + graphicsStack.size(), e);
            }
        }
        graphicsStack = null;
        infoStack = null;
    }

    public Graphics2D getGraphics() {
        return graphicsStack.get(graphicsStack.size() - 1);
    }

    public DrawInfo getInfo() {
        return infoStack.get(infoStack.size() - 1);
    }

    public SpaceTransformation getSpaceTransformation() {
        return this.spaceTransformation;
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
        graphicsStack.add((Graphics2D) getGraphics().create());
        infoStack.add(getInfo().clone());
    }
}

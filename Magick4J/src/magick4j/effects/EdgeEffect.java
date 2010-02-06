package magick4j.effects;

import java.awt.image.Kernel;
import magick4j.MagickImage;
import magick4j.exceptions.OptionException;

public class EdgeEffect extends BasicEffect {
    
    private double radius;

    public EdgeEffect(double radius){
        this.radius = radius;
    }

    @Override
    protected MagickImage effect(MagickImage image)
            throws OptionException{
        int width = getOptimalKernelWidth1D(radius, 0.5);
        int size = width * width;
        float[] kernel = new float[size];
        int i = 0;
        for (i = 0; i < size; i++) {
            kernel[i] = -1.0f;
        }
        kernel[i / 2] = size - 1.0f;
        return convolve(image, width, new Kernel(width, width, kernel));
    }

}

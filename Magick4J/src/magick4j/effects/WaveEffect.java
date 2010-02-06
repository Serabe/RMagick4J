/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magick4j.effects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import magick4j.MagickImage;
import magick4j.exceptions.OptionException;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.sin;

import static magick4j.Constants.MagickPI;
import static magick4j.Constants.QuantumRange;
import static magick4j.Constants.QuantumScale;
import static magick4j.Constants.magickSigma;

/**
 *
 * @author serabe
 */
public class WaveEffect extends BasicEffect{

    private double amplitude;
    private double waveLength;

    public WaveEffect(double amplitude, double waveLength){
        this.amplitude = amplitude;
        this.waveLength = waveLength;
    }

    @Override
    protected MagickImage effect(MagickImage image) throws OptionException {
        MagickImage wave = image.createCompatible(image.getWidth(), image.getHeight()+(int) (2.0*abs(this.amplitude)));

        double[] sineMap = getSineMap(image.getWidth());
        double[] pixel = new double[4];
        int x,y;
        int w = wave.getWidth(), h = wave.getHeight();

        WritableRaster o = wave.getImage().getRaster();

        BufferedImage expanded = new BufferedImage(w+1,h+(int) (2*abs(this.amplitude))+1,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = expanded.createGraphics();
        g.setBackground(image.getBackgroundColor().toColor());
        g.clearRect(0, 0, w, h);
        g.drawImage(image.getImage(), 0, (int) (2*this.amplitude), image.getWidth(), image.getHeight(), null);
        g.dispose();

        double[] q = new double[w*4];
        double[] pixels = new double[16];
        double[] alpha = new double[4];
        double[] delta = new double[2];
        double[] epsilon = new double[2];
        double gamma;

        for(y = 0; y < h; y++){
            
            q = new double[w*4];
            pixel[0] = pixel[1] = pixel[2] = pixel[3] = 0;

            for(x = 0; x < w; x++){
                //pixel = rf.resamplePixelColor(x, y-sineMap[x], expanded, offsets, pixel);
                pixels = expanded.getRaster().getPixels((int) floor(x), (int)(2*this.amplitude)+(int) floor(y-sineMap[x]), 2, 2, pixels);
                alpha[0] = alpha[1] = alpha[2] = alpha[3] = 1.0;

                if(/*image.getMatte()*/ true){
                    alpha[0] = QuantumScale*pixels[3];
                    pixels[0] *= alpha[0];
                    pixels[1] *= alpha[0];
                    pixels[2] *= alpha[0];
                    alpha[1] = QuantumScale*pixels[7];
                    pixels[4] *= alpha[1];
                    pixels[5] *= alpha[1];
                    pixels[6] *= alpha[1];
                    alpha[2] = QuantumScale*pixels[11];
                    pixels[8] *= alpha[2];
                    pixels[9] *= alpha[2];
                    pixels[10] *= alpha[2];
                    alpha[3] = QuantumScale*pixels[15];
                    pixels[12] *= alpha[3];
                    pixels[13] *= alpha[3];
                    pixels[14] *= alpha[3];
                }

                delta[0] = x - floor(x);
                delta[1] = y - floor(y);
                epsilon[0] = 1.0 - delta[0];
                epsilon[1] = 1.0 - delta[1];

                gamma = ((epsilon[1]*(epsilon[0]*alpha[0]+delta[0]*alpha[1])+delta[1]*(epsilon[0]*alpha[2]+delta[0]*alpha[3])));
                gamma = 1.0/magickSigma(gamma);

                pixel[0] =  gamma*(epsilon[1]*(epsilon[0]*pixels[0*4+0]+delta[0]
                            *pixels[1*4+0])+delta[1]*(epsilon[0]*pixels[2*4+0]+
                            delta[0]*pixels[3*4+0]));
                pixel[1] =  gamma*(epsilon[1]*(epsilon[0]*pixels[0*4+1]+delta[0]
                            *pixels[1*4+1])+delta[1]*(epsilon[0]*pixels[2*4+1]+
                            delta[0]*pixels[3*4+1]));
                pixel[2] =  gamma*(epsilon[1]*(epsilon[0]*pixels[0*4+2]+delta[0]
                            *pixels[1*4+2])+delta[1]*(epsilon[0]*pixels[2*4+2]+
                            delta[0]*pixels[3*4+2]));
                pixel[3] =  (QuantumRange - gamma*(epsilon[1]*(epsilon[0]*(QuantumRange - pixels[0*4+3])+delta[0]
                            *(QuantumRange - pixels[1*4+3]))+delta[1]*(epsilon[0]*(QuantumRange - pixels[2*4+3])+
                            delta[0]*(QuantumRange - pixels[3*4+3]))));

                System.arraycopy(pixel, 0, q, x*4, 4);
            }

            o.setPixels(0, y, w, 1, q);

        }

        sineMap = null;
        return wave;
    }

    private double[] getSineMap(int length) {
        double[] sm = new double[length];
        for(int i = 0; i < length; i++)
           sm[i] = abs(this.amplitude)+this.amplitude*sin((2*MagickPI*i)/this.waveLength);
        return sm;
    }

}

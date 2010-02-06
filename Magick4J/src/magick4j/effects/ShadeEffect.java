package magick4j.effects;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import magick4j.Constants;
import magick4j.MagickImage;
import magick4j.PixelPacket;
import magick4j.exceptions.OptionException;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

/**
 *
 * @author serabe
 */
public class ShadeEffect extends BasicEffect{

	private boolean shading;
	private double azimuth;
	private double elevation;

	public ShadeEffect(boolean shading, double azimuth, double elevation){
		this.shading = shading;
		this.azimuth = azimuth;
		this.elevation = elevation;
	}

	@Override
	protected MagickImage effect(MagickImage image) throws OptionException {
		double[] light = this.calculateLightVector();

		BufferedImage orig = image.getImageToConvolve(3);
		MagickImage dst = image.createCompatible();

		int h = image.getHeight(), w = image.getWidth();
		int cw = w +2;
		int lcw = cw*4;
		int base;

		double distance, normalDistance, shade;
		double[] normal = new double[3];

		double[][] p = new double[3][lcw];
		double[] q = new double[w*4];
		double[][] s = new double[3][cw];

		WritableRaster o = orig.getRaster();
		WritableRaster d = dst.getImage().getRaster();

		p[1] = o.getPixels(0, 0, cw, 1, p[1]);
		p[2] = o.getPixels(0, 1, cw, 1, p[2]);

		for(int i = 0; i < cw; i++){
			base = 4*i;
			s[1][i] = PixelPacket.calculateIntensity(p[1], base);
			s[2][i] = PixelPacket.calculateIntensity(p[2], base);
		}

		for(int y = 0; y < h; y++){

			// Slide the p array.
			System.arraycopy(p[1], 0, p[0], 0, lcw);
			System.arraycopy(p[2], 0, p[1], 0, lcw);
			p[2] = o.getPixels(0, y+2, cw, 1, p[2]);

			// Slide the s array.
			System.arraycopy(s[1], 0, s[0], 0, cw);
			System.arraycopy(s[2], 0, s[1], 0, cw);
			for(int i = 0; i < cw; i++){
				base = 4*i;
				s[2][i] = PixelPacket.calculateIntensity(p[2], base);
			}

			normal[2] = 2.0*Constants.QuantumRange;

			for(int x = 0; x < w; x++){
				normal[0] = s[0][x]+s[1][x]+s[2][x]-s[0][x+2]-s[1][x+2]-s[2][x+2];
				normal[1] = s[2][x]+s[2][x+1]+s[2][x+2]-s[0][x]-s[0][x+1]-s[0][x+2];

				if(normal[0] == 0.0 && normal[1] == 0.0)
					shade = light[2];
				else{
					shade = 0.0;
					distance = normal[0]*light[0]+normal[1]*light[1]+normal[2]*light[2];

					if(distance > Constants.MagickEpsilon){
						normalDistance = normal[0]*normal[0] + normal[1]*normal[1] + normal[2]*normal[2];
						if(normalDistance > Constants.MagickEpsilon*Constants.MagickEpsilon)
							shade = distance/sqrt(normalDistance);
					}
				}
				base = x*4;
				if(this.shading){
					q[base+0] = shade;
					q[base+1] = shade;
					q[base+2] = shade;
				}else{
					q[base+0] = roundToQuantum(Constants.QuantumScale*shade*p[1][base+4+0]);
					q[base+1] = roundToQuantum(Constants.QuantumScale*shade*p[1][base+4+1]);
					q[base+2] = roundToQuantum(Constants.QuantumScale*shade*p[1][base+4+2]);
				}

				q[base+3] = p[1][base+4+3];
			}

			d.setPixels(0, y, w, 1, q);

		}

		return dst;
	}

	protected double[] calculateLightVector(){
		double azimuthRadians = toRadians(this.azimuth);
		double elevationRadians = toRadians(this.elevation);

		double[] light = new double[3];

		light[0] = Constants.QuantumRange*cos(azimuthRadians)*cos(elevationRadians);
		light[1] = Constants.QuantumRange*sin(azimuthRadians)*cos(elevationRadians);
		light[2] = Constants.QuantumRange*sin(elevationRadians);

		return light;
	}
}

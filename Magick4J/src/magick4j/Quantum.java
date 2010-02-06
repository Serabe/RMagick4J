package magick4j;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

/**
 *
 * @author serabe
 */
public class Quantum {

	public static int roundToQuantum(double x){
		if( x <= 0.0){
			return 0;
		} else if( x >= Constants.QuantumRange){
			return (int) round(Constants.QuantumRange);
		} else {
			return (int) (x+0.5);
 		}
	}

	public static double scaleQuantumToMap(double x){
		return max(0, min(Constants.MaxMap, x));
	}
}

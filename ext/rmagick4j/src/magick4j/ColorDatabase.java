package magick4j;

import java.util.*;

/**
 * TODO Should this be an interface with multiple implementations or is that overkill?
 */
public class ColorDatabase {

	private static final Map<String, String> NAMED_COLORS = buildNamedColors();

	private static Map<String, String> buildNamedColors() {
		// TODO Maybe read from a properties file instead.
		Map<String, String> colors = new HashMap<String, String>();
		colors.put("black", "000000");
		colors.put("green", "00FF00");
		colors.put("red", "FF0000");
		colors.put("white", "FFFFFF");
		return colors;
	}

	public static PixelPacket queryDefault(String colorName) {
		return new ColorDatabase().query(colorName);
	}

	private PixelPacket lookUp(String colorName) {
		// TODO Make PixelPacket cloneable and store PixelPackets in NAMED_COLORS instead of Strings?
		// TODO Should "#rrggbb" also support alpha at the end?
		if (colorName.equals("transparent")) {
			return new PixelPacket(0, 0, 0, 0);
		}
		String oldName = colorName;
		colorName = NAMED_COLORS.get(colorName);
		if (colorName == null) System.err.println("not found: " + oldName);
		return colorName == null ? null : parseRgb(colorName);
	}

	private PixelPacket parseRgb(String rgb) {
		if (rgb.length() == 6) {
			// If NumberFormatException, that's okay. Crazy input.
			// Just need to be able to say this in Ruby:
			// ArgumentError: invalid color name: #eerr00
			double red = Integer.parseInt(rgb.substring(0, 2), 16);
			double green = Integer.parseInt(rgb.substring(2, 4), 16);
			double blue = Integer.parseInt(rgb.substring(4, 6), 16);
			return new PixelPacket(red / 255, green / 255, blue / 255);
		}
		return null;
	}

	public PixelPacket query(String colorName) {
		if (colorName.startsWith("#")) {
			return parseRgb(colorName.substring(1));
		} else {
			return lookUp(colorName);
		}
	}

}

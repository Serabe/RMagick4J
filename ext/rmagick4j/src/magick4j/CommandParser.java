package magick4j;

import static java.lang.Double.*;
import static java.lang.Integer.*;
import static java.lang.Math.*;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * See http://studio.imagemagick.org/script/magick-vector-graphics.php for specs.
 */
public class CommandParser {

	private interface ParserBuilder {
		Command build(String... parts);
	}

	private static final Map<String, ParserBuilder> BUILDERS = buildBuilders();

	private static Map<String, ParserBuilder> buildBuilders() {
		Map<String, ParserBuilder> builders = new HashMap<String, ParserBuilder>();
		builders.put("circle", new ParserBuilder() {
			public Command build(String... parts) {
				String[] args0 = parts[1].split(",");
				String[] args1 = parts[2].split(",");
				double centerX = parseDouble(args0[0]);
				double centerY = parseDouble(args0[1]);
				double perimeterX = parseDouble(args1[0]);
				double perimeterY = parseDouble(args1[1]);
				double dX = perimeterX - centerX;
				double dY = perimeterY - centerY;
				double radius = sqrt(dX * dX + dY * dY);
				return CommandBuilder.shape(new Ellipse2D.Double(centerX - radius, centerY - radius, 2 * radius, 2 * radius));
			}
		});
		builders.put("ellipse", new ParserBuilder() {
			public Command build(String... parts) {
				String[] args0 = parts[1].split(",");
				String[] args1 = parts[2].split(",");
				String[] args2 = parts[3].split(",");
				double centerX = parseDouble(args0[0]);
				double centerY = parseDouble(args0[1]);
				double radiusX = parseDouble(args1[0]);
				double radiusY = parseDouble(args1[1]);
				double arcStart = parseDouble(args2[0]);
				double arcStop = parseDouble(args2[1]);
				// TODO Custom primitive class to support OPEN strokes and PIE fills?
				return CommandBuilder.shape(new Arc2D.Double(centerX - radiusX, centerY - radiusY, 2 * radiusX, 2 * radiusY, -arcStart, -(arcStop - arcStart), Arc2D.OPEN));
			}
		});
		builders.put("fill", new ParserBuilder() {
			public Command build(String... parts) {
				String colorName = parts[1].replace("\"", "");
				return CommandBuilder.fill(ColorDatabase.queryDefault(colorName));
			}
		});
		builders.put("fill-opacity", new ParserBuilder() {
			public Command build(String... parts) {
				double opacity = parseDouble(parts[1]);
				return CommandBuilder.fillOpacity(opacity);
			}
		});
		builders.put("line", new ParserBuilder() {
			public Command build(String... parts) {
				String[] args0 = parts[1].split(",");
				String[] args1 = parts[2].split(",");
				double x1 = parseDouble(args0[0]);
				double y1 = parseDouble(args0[1]);
				double x2 = parseDouble(args1[0]);
				double y2 = parseDouble(args1[1]);
				// TODO Custom primitive to avoid fills?
				return CommandBuilder.shape(new Line2D.Double(x1, y1, x2, y2));
			}
		});
		builders.put("polygon", new ParserBuilder() {
			public Command build(String... parts) {
				GeneralPath path = buildPolyline(parts);
				path.closePath();
				return CommandBuilder.shape(path);
			}
		});
		builders.put("polyline", new ParserBuilder() {
			public Command build(String... parts) {
				GeneralPath path = buildPolyline(parts);
				return CommandBuilder.shape(path);
			}
		});
		builders.put("pop", new ParserBuilder() {
			public Command build(String... parts) {
				String type = parts[1];
				if (type.equals("graphic-context")) {
					return CommandBuilder.pop();
				} else {
					throw new RuntimeException("unknown pop type: " + type);
				}
			}
		});
		builders.put("push", new ParserBuilder() {
			public Command build(String... parts) {
				String type = parts[1];
				if (type.equals("graphic-context")) {
					return CommandBuilder.push();
				} else {
					throw new RuntimeException("unknown push type: " + type);
				}
			}
		});
		builders.put("rectangle", new ParserBuilder() {
			public Command build(String... parts) {
				String[] args0 = parts[1].split(",");
				String[] args1 = parts[2].split(",");
				double x1 = parseDouble(args0[0]);
				double y1 = parseDouble(args0[1]);
				double x2 = parseDouble(args1[0]);
				double y2 = parseDouble(args1[1]);
				return CommandBuilder.shape(new Rectangle2D.Double(min(x1, x2), min(y1, y2), abs(x2 - x1), abs(y2 - y1)));
			}
		});
		builders.put("roundrectangle", new ParserBuilder() {
			public Command build(String... parts) {
				String[] args = parts[1].split(",");
				double x1 = parseDouble(args[0]);
				double y1 = parseDouble(args[1]);
				double x2 = parseDouble(args[2]);
				double y2 = parseDouble(args[3]);
				double cornerWidth = parseDouble(args[4]);
				double cornerHeight = parseDouble(args[5]);
				return CommandBuilder.shape(new RoundRectangle2D.Double(x1, y1, x2 - x1, y2 - y1, cornerWidth, cornerHeight));
			}
		});
		builders.put("scale", new ParserBuilder() {
			public Command build(String... parts) {
				String[] args = parts[1].split(",");
				return CommandBuilder.scale(parseDouble(args[0]), parseDouble(args[1]));
			}
		});
		builders.put("stroke", new ParserBuilder() {
			public Command build(String... parts) {
				String colorName = parts[1].replace("\"", "");
				return CommandBuilder.stroke(ColorDatabase.queryDefault(colorName));
			}
		});
		builders.put("stroke-antialias", new ParserBuilder() {
			public Command build(String... parts) {
				return CommandBuilder.strokeAntialias(parseInt(parts[1]) == 1);
			}
		});
		builders.put("stroke-dasharray", new ParserBuilder() {
			public Command build(String... parts) {
				String[] args = parts[1].split(",");
				double[] lengths = new double[args.length];
				for (int a = 0; a < args.length; a++) {
					lengths[a] = parseDouble(args[a]);
				}
				return CommandBuilder.strokeDashArray(lengths);
			}
		});
		builders.put("stroke-opacity", new ParserBuilder() {
			public Command build(String... parts) {
				return CommandBuilder.strokeOpacity(parseDouble(parts[1]));
			}
		});
		builders.put("stroke-width", new ParserBuilder() {
			public Command build(String... parts) {
				return CommandBuilder.strokeWidth(parseDouble(parts[1]));
			}
		});
		return builders;
	}

	private static GeneralPath buildPolyline(String parts[]) {
		GeneralPath path = new GeneralPath();
		String[] args = parts[1].split(",");
		for (int a = 0; a < args.length; a += 2) {
			double x = parseDouble(args[a]);
			double y = parseDouble(args[a + 1]);
			if (a == 0) {
				path.moveTo((float)x, (float)y);
			} else {
				path.lineTo((float)x, (float)y);
			}
		}
		return path;
	}

	public static List<Command> parse(String script) {
		try {
			List<Command> commands = new ArrayList<Command>();
			BufferedReader reader = new BufferedReader(new StringReader(script));
			// TODO MVG specs say this isn't really line based. They could all be on one line.
			String line;
			while ((line = reader.readLine()) != null) {
				commands.add(parseCommand(line));
			}
			// Officially no need to close StringReaders.
			return commands;
		} catch (Exception e) {
			throw Thrower.throwAny(e);
		}
	}

	private static Command parseCommand(String text) {
		String[] parts = text.split(" +");
		String command = parts[0];
		ParserBuilder builder = BUILDERS.get(command);
		if (builder == null) {
			// TODO This should also be the error for bad params (at least when I tested roundrectangle with only 2)
			// Magick::ImageMagickError: Non-conforming drawing primitive definition `yodle'
			// irb(main):018:0> Magick::ImageMagickError.superclass
			// => StandardError
			// TODO Also, this text might ought to be in the Ruby side, not in the Java side.
			throw new RuntimeException("Non-conforming drawing primitive definition `" + command + "'");
		}
		return builder.build(parts);
	}

}

package magick4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFormatter {
    private static final Pattern PATTERN = Pattern.compile("[%\\\\].");
    private MagickImage image;

    public TextFormatter(MagickImage image) {
        this.image = image;
    }

    public String format(String text) {
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.find()) {
            StringBuffer buffer = new StringBuffer();
            do {
                int c = matcher.group().codePointAt(1);
                String replacement;
                switch (c) {
                    case '%':
                        replacement = "%";
                        break;
                    default:
                        replacement = "";
                        break;
                }
                matcher.appendReplacement(buffer, replacement);
            } while (matcher.find());
            matcher.appendTail(buffer);
            text = buffer.toString();
        }
        return text;
    }
}

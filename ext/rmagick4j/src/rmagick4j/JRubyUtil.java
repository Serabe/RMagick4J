package rmagick4j;

import org.jruby.*;

public class JRubyUtil {

	public static byte[] toByteArray(String string) {
		return RubyString.stringToBytes(string);
	}

	public static String toString(byte[] bytes) {
		return RubyString.bytesToString(bytes);
	}

}

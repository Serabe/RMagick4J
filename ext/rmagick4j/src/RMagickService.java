import java.io.*;

import org.jruby.*;
import org.jruby.runtime.load.*;

public class RMagickService implements BasicLibraryService {

	public boolean basicLoad(Ruby runtime) throws IOException {
		runtime.evalScriptlet("require 'rmagick4j/rmagick4j.rb'");
		return true;
	}

}

import java.io.IOException;

import org.jruby.Ruby;
import org.jruby.runtime.load.BasicLibraryService;


public class RMagickService implements BasicLibraryService {
    public boolean basicLoad(Ruby runtime) throws IOException {
        runtime.evalScriptlet("require 'rmagick4j/rmagick4j.rb'");
        
        return true;
    }
}

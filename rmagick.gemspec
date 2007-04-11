require 'date'
Gem::Specification.new do |s|
  s.name = %q{rmagick}
  s.version = '1.15.4'
  s.date = Date.today.to_s
  s.summary = %q{RMagick is an interface between the Ruby programming language and the ImageMagick, GraphicsMagick, and Magick4J image processing libraries.}
  s.description =<<DESCRIPTION
RMagick is an interface between the Ruby programming language and the
ImageMagick, GraphicsMagick, and Magick4J image processing libraries.
DESCRIPTION
  s.author = %q{Tim Hunter - RMagick4J by Tom Palmer}
  s.email = %q{jruby-extras-devel@rubyforge.org}
  s.homepage = %q{http://rubyforge.org/projects/jruby-extras/}
  s.files = Dir['lib/**']
  s.platform = 'java'
  s.rubyforge_project = %q{jruby-extras}
  s.has_rdoc = false
end

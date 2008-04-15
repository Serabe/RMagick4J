require 'date'
Gem::Specification.new do |s|
  s.name = %q{rmagick4j}
  s.version = '0.3.4'
  s.date = Date.today.to_s
  s.summary = %q{RMagick4J is a JRuby back end for RMagick.}
  s.description =<<DESCRIPTION
RMagick4J is a JRuby back end to support the RMagick library.
It bundles a Java library called Magick4J that implements ImageMagick
and some RMagick native functionality.
DESCRIPTION
  s.author = %q{Tom Palmer & Serabe}
  s.email = %q{jruby-extras-devel@rubyforge.org}
  s.homepage = %q{http://code.google.com/p/rmagick4j/}
  s.files = Dir['lib/*'] | Dir['lib/*/*']
  s.platform = 'java'
  s.rubyforge_project = %q{jruby-extras}
  s.has_rdoc = false
end

if PLATFORM == 'java'
  require File.join(File.dirname(__FILE__), '..', '..', '..','lib','rmagick4j','rmagick4j')
else
  require "rubygems"
  require "RMagick"
end

class Magick::Image
  alias_method :old_write, :write
  
  def self.load(filename, &add)
    file = File.join(File.expand_path(File.dirname(__FILE__)), '..', '..', 'images', filename)
    self.read file, &add
  end
  
  def write(filename)
    engine = (PLATFORM == 'java' ? 'jruby' : 'mri')
    new_filename = filename.sub(/(\.[^\.]*)$/, '.' + engine + '\1')
    old_write File.join(File.expand_path(File.dirname(__FILE__)), '..', 'images',new_filename)
  end
end
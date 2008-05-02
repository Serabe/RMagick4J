if PLATFORM == 'java'
  require File.join(File.dirname(__FILE__), '..', '..','lib','rmagick4j','rmagick4j')
else
  require "rubygems"
  require "RMagick"
end

class Magick::Image
  alias_method :old_write, :write
  
  def write(filename)
    index = filename.index('.',-5)
    
    new_filename =  if PLATFORM == 'java'
                      filename[0..index] + 'jruby' + filename[index..filename.size]
                    else
                      filename[0..index] + 'mri' + filename[index..filename.size]
                    end
    old_write File.join('images',new_filename)
  end
end
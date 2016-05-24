if PLATFORM == 'java'
  require File.join(File.dirname(__FILE__), '..', '..', '..', 'lib', 'rmagick')
else
  require 'rubygems'
  require 'rmagick'
end

class Float
  def to_threshold
    self/255 * Magick::QuantumRange
  end
end

class Magick::Image
  OUTPUT_DIR = File.join(File.dirname(File.dirname(__FILE__)), 'examples')
  FileUtils.makedirs OUTPUT_DIR
  alias_method :old_write, :write

  def self.load(filename, &add)
    file = File.join(File.expand_path(File.dirname(__FILE__)), '..', '..', 'images', filename)
    self.read file, &add
  end

  def write(filename)
    engine = (PLATFORM == 'java' ? 'jruby' : 'mri')
    new_filename = filename.sub(/(\.[^\.]*)$/, '.' + engine + '\1')
    change_geometry('300x300') do |columns, rows, image|
      image.resize!(columns, rows)
    end
    old_write File.join(OUTPUT_DIR, new_filename)
  end
end

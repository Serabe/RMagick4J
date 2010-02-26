if PLATFORM == 'java'
  require File.join(File.dirname(__FILE__), '..', '..', '..','lib','RMagick')
else
  require "rubygems"
  require "RMagick"
end

class Float

	def to_threshold
		self/255 * Magick::QuantumRange
	end
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
    change_geometry('300x300') do |columns, rows, image|
      image.resize!(columns, rows)
    end
    old_write File.join(ARGV[0],new_filename)
  end
end

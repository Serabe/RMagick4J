require File.join(File.dirname(__FILE__),'..','lib','magick4j')
require File.join(File.dirname(__FILE__),'..','lib','rmagick4j','constants')
require File.join(File.dirname(__FILE__),'..','lib','rmagick4j','image')
require File.join(File.dirname(__FILE__),'spec','image_constants.rb')

include Magick

@image = Image.read(File.join(File.dirname(__FILE__),'clown.jpg'))

puts 'Image class'

num_implemented_methods = 0

IMAGE_METHODS.each do |method|
  num_implemented_methods += 1 if @image.respond_to? method.to_sym
end

puts "Implemented #{num_implemented_methods}/#{IMAGE_METHODS_SIZE} (#{100.0*num_implemented_methods.to_f/IMAGE_METHODS_SIZE.to_f}%)."
require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')

g = Gruff::Area.new
g.title = "Many Multi-Area Graph Test"
g.labels = {
  0 => 'June', 
  10 => 'July', 
  30 => 'August', 
  50 => 'September', 
}
g.data('many points', (0..50).collect {|i| rand(100) })

# Default theme
g.write("gruff_area_1.jpg")
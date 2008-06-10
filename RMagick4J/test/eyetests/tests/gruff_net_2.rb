require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')

g = Gruff::Net.new
g.title = "Many Multi-Net Graph Test"
g.labels = {
  0 => 'June', 
  10 => 'July', 
  30 => 'August', 
  50 => 'September', 
}
g.data('many points', (0..50).collect {|i| rand(100) })

# Default theme
g.write("gruff_net_2.jpg")
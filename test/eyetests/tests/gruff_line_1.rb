require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')

g = Gruff::Line.new
g.title = "One Value"
g.labels = {
  0 => '1', 
  1 => '2'
}
g.data('one', 1)

g.write("gruff_line_1.jpg")

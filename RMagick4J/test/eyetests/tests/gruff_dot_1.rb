require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')

g = Gruff::Dot.new
g.title = "Dot Graph With Manual Colors"
g.labels = {
						0 => '5/6',
						1 => '5/15',
						2 => '5/24',
						3 => '5/30',
					 }
g.data(:Art, [0, 5, 8, 15], '#990000')
g.data(:Philosophy, [10, 3, 2, 8], '#009900')
g.data(:Science, [2, 15, 8, 11], '#990099')

g.minimum_value = 0

g.write("gruff_dot_1.jpg")

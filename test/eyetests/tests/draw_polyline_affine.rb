require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.stroke = 'blue'
draw.stroke_width 5.0
draw.affine(1,0,0,1,-60,100)

points = [350,75,379,161,469,161,397,215,423,301,350,250,277,301,303,215,231,161,321,161].collect { |x| x/2.round }

draw.polyline(*points)

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw b

b.write('draw_polyline_affine.jpg')
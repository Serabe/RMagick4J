require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.stroke = 'red'
draw.fill_opacity 0.0

draw.arc(40, 50, 250, 180, 0, 270)

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw b

b.write('draw_arc_basic.jpg')
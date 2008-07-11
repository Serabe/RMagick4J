require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.stroke = 'blue'
draw.stroke_linejoin 'miter'
draw.stroke_miterlimit 5.0
draw.stroke_width 5.0
draw.fill = 'red'

points = [125,300,175,300,150,50]

draw.polygon(*points)

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw b

b.write('draw_stroke_miterlimit.jpg')
require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.stroke = 'red'
draw.stroke_linecap 'round'
draw.stroke_width 10.0

draw.line(50, 50, 50, 200)
draw.stroke('blue')
draw.line(50, 200, 200, 200)
draw.stroke('green')
draw.line(200, 200, 50, 50)

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw b

b.write('draw_line_basic.jpg')
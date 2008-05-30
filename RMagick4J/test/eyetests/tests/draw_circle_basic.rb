require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.stroke = 'LightCyan2'
draw.fill = 'LightCyan2'

draw.circle(100,100, 200,180)

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw b

b.write('draw_circle_basic.jpg')
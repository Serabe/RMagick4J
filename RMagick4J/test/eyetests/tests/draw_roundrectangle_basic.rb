require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.stroke = 'LightCyan2'
draw.fill 'LightCyan2'

draw.roundrectangle(20,20, 280,180, 8, 8)

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw b

b.write('draw_roundrectangle_basic.jpg')
require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.stroke = 'green'

draw.rectangle(200,20, 80,180)

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw b

b.write('draw_rectangle_basic.jpg')

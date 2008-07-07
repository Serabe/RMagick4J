require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.fill 'red'
draw.stroke 'blue'
draw.stroke_width(5)
draw.path 'M150,150 h-140 a140,100 45 1,0 140,-140'

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw(b)

b.write('path_a_command_01.jpg')
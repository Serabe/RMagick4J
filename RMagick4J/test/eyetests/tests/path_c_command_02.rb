require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.fill 'red'
draw.stroke 'blue'
draw.stroke_width(5)
draw.path 'M50,150 c0,-150 200,150 200,0'

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw(b)

b.write('path_c_command_02.jpg')
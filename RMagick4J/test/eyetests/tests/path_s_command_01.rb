require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.fill 'red'
draw.stroke 'blue'
draw.stroke_width(5)
draw.path 'M0,150 C0,0 100,0 100,150S200,300 200,150s100,-150 100,0'

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw(b)

b.write('path_s_command_01.jpg')
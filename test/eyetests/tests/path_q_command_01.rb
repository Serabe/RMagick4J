require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.fill 'red'
draw.stroke 'blue'
draw.stroke_width(5)
draw.path 'M0,150 Q75,0 150,150 225,300 300,150'

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw(b)

b.write('path_q_command_01.jpg')
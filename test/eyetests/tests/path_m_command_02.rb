require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.fill 'red'
draw.stroke 'blue'
draw.stroke_width(5)
draw.path 'M50 50 100 100 0 100M190 190 290 190 290 290 190 290'

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw(b)

b.write('path_m_command_02.jpg')
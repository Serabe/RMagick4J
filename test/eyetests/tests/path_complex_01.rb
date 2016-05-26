require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.fill 'GREENYELLOW'
draw.stroke 'DarkBlue'
draw.stroke_width(5)
draw.path 'M150 150 200 200v25C225 225 250 250 200 250S150 275 200 275H100Z'

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw(b)

b.write('path_complex_01.jpg')
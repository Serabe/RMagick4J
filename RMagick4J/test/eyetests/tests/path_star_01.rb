require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.fill 'red'

draw.fill_rule 'nonzero'

draw.stroke 'blue'
draw.stroke_width(5)
draw.path 'M100,10 L100,10 40,180 190,60 10,60 160,180 z'

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw(b)

b.write('path_star_01.jpg')
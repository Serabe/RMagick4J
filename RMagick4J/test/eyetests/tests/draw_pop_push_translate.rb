require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.translate 100, 100
draw.circle 100,100, 100, 50
draw.stroke 'blue'
draw.fill 'white'
draw.push
draw.circle 100,100, 100, 50
draw.translate(-100, -100)
draw.fill 'green'
draw.pop
draw.circle 100, 100, 100, 50

b = Image.new(300, 300)

draw.draw b

b.write('draw_pop_push_translate.jpg')

require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.stroke = 'LightCyan2'
draw.fill = 'LightCyan2'
draw.affine(2,0,0,3,0,0) # Scale 2x 3y
angle = 15*Math::PI/180
draw.affine(Math.cos(angle),-Math.sin(angle),Math.sin(angle),Math.cos(angle),0,0)

draw.circle(50,50, 50,70)

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw b

b.write('draw_circle_affine.jpg')
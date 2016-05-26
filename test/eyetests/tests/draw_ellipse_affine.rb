require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.stroke = 'red'
draw.fill_opacity 0.0
draw.affine(2,0,0,3,0,0) # Scale 2x 3y
angle = 15*Math::PI/180
draw.affine(Math.cos(angle),-Math.sin(angle),Math.sin(angle),Math.cos(angle),0,0)

draw.ellipse(180,125, 150,75, 90, 270)

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw b

b.write('draw_ellipse_affine.jpg')

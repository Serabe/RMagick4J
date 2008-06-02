require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.stroke = 'LightCyan2'
draw.fill 'LightCyan2'
draw.affine(2,0,0,3,50,20)
angle = 15*Math::PI/180
draw.affine(Math.cos(angle),-Math.sin(angle),Math.sin(angle),Math.cos(angle),0,0)

draw.roundrectangle(20,20, 280,180, 8, 8)

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw b

b.write('draw_roundrectangle_affine.jpg')
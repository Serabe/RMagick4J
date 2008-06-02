require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.stroke = 'red'
draw.stroke_width 10.0
draw.line(50, 50, 50, 200)

draw.stroke('blue')
draw.affine(2,0,0,3,0,0) # Scale 2x 3y
draw.line(50, 200, 200, 200)

angle = 15*Math::PI/180
draw.affine(Math.cos(angle),-Math.sin(angle),Math.sin(angle),Math.cos(angle),0,0)
draw.stroke('green')
draw.line(200, 200, 50, 50)

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw b

b.write('draw_line_affine.jpg')
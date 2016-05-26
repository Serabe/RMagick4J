require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

canvas = Image.new(240, 300,
              HatchFill.new('white','lightcyan2'))
gc = Draw.new

gc.stroke('#001aff')
gc.stroke_width(3)
gc.fill('#00ff00')

x = 120
y = 32.5
gc.polygon(x,    y,     x+29, y+86,  x+109, y+86,
           x+47, y+140, x+73, y+226, x,     y+175,
           x-73, y+226, x-47, y+140, x-109, y+86,
           x-29, y+86)

gc.draw(canvas)
canvas.write('draw_rmagick_test_04.jpg')

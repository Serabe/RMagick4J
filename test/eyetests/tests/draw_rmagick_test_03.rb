require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

canvas = Image.new(300, 300,
              HatchFill.new('white','lightcyan2'))
gc = Draw.new

gc.fill('red')
gc.stroke('blue')
gc.stroke_width(2)
gc.path('M120,150 h-75 a75,75 0 1, 0 75,-75 z')
gc.fill('yellow')
gc.path('M108.5,138.5 v-75 a75,75 0 0,0 -75,75 z')
gc.draw(canvas)

canvas.write('draw_rmagick_test_03.jpg')

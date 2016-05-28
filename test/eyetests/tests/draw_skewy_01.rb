require File.join(File.dirname(__FILE__), 'new_image.rb')

# Taken from RMagick documentation. Modified.

imgl = Magick::Image.new(300, 300, Magick::HatchFill.new('white','lightcyan2'))

gc = Magick::Draw.new

# Move the origin to the center.
gc.translate(125, 125)
max_x = imgl.columns/2
max_y = imgl.rows/2

# Skew y 30 degrees
gc.skewy(30)

# Draw down-pointing arrow
gc.fill('gray50')

gc.line(0, -max_y,   0, max_y)
gc.line(0,  max_y,  10, max_y-10)
gc.line(0,  max_y, -10, max_y-10)

# Draw right-pointing arrow
gc.stroke('red')
gc.stroke_width(3)
gc.line(-max_x+10, 0, max_x-10,   0)
gc.line( max_x-10, 0, max_x-20, -10)
gc.line( max_x-10, 0, max_x-20,  10)

gc.draw(imgl)

imgl.write("draw_skewy_01.jpg")

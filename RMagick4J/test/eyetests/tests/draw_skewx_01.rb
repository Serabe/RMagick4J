require File.join(File.dirname(__FILE__), 'new_image.rb')

# Taken from RMagick documentation. Modified.

imgl = Magick::Image.new(250, 250, Magick::HatchFill.new('white','lightcyan2'))

gc = Magick::Draw.new

# Move the origin to the center.
gc.translate(125, 125)
max_x = imgl.columns/2
max_y = imgl.rows/2 - 5

# Skew x 30 degrees
gc.skewx(30)

# Draw down-pointing arrow
gc.fill('red')
gc.stroke('red')
gc.stroke_width(3)
gc.line(0, -max_y,  0, max_y)
gc.line(0,  max_y,  7, max_y-7)
gc.line(0,  max_y, -7, max_y-7)

# Draw right-pointing arrow
gc.stroke('gray50')
gc.stroke_width(1)
gc.line(-max_x, 0, max_x,    0)
gc.line( max_x, 0, max_x-5, -5)
gc.line( max_x, 0, max_x-5,  5)

gc.draw(imgl)

imgl.write("draw_skewx_01.jpg")
require File.join(File.dirname(__FILE__), 'new_image.rb')

# Taken from RMagick documentation. Modified.

imgl = Magick::Image.new(200, 200, Magick::HatchFill.new('white','lightcyan2'))

gc = Magick::Draw.new

# Move the origin to the center.
gc.translate(100, 100)
max_x = imgl.columns/2
max_y = imgl.rows/2

# Scale by 0.5 and 1/3.
gc.scale(0.5, 1.0/3.0)

gc.stroke('red')
gc.stroke_width(3)

# Draw down-pointing arrow
gc.line(0, -max_y,   0, max_y)
gc.line(0,  max_y,  10, max_y-10)
gc.line(0,  max_y, -10, max_y-10)

# Draw right-pointing arrow
gc.line(-max_x, 0, max_x,     0)
gc.line( max_x, 0, max_x-10, -10)
gc.line( max_x, 0, max_x-10,  10)


gc.draw(imgl)

imgl.write("draw_scale_01.jpg")
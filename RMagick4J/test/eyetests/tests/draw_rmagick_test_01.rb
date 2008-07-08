require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

canvas = Image.new(240, 300,
              HatchFill.new('white','lightcyan2'))
gc = Draw.new

# Draw ellipse
gc.stroke('red')
gc.stroke_width(3)
gc.fill_opacity(0)
gc.ellipse(120, 150, 80, 120, 0, 270)

# Draw endpoints
gc.stroke('gray50')
gc.stroke_width(1)
gc.circle(120, 150, 124, 150)
gc.circle(200, 150, 204, 150)
gc.circle(120,  30, 124,  30)

# Draw lines
gc.line(120, 150, 200, 150)
gc.line(120, 150, 120,  30)

# Annotate (not yet implemented)
#gc.stroke('transparent')
#gc.fill('black')
#gc.text(130, 35, "End")
#gc.text(188, 135, "Start")
#gc.text(130, 95, "'Height=120'")
#gc.text(55, 155, "'Width=80'")

gc.draw(canvas)
canvas.write('draw_rmagick_test_01.jpg')

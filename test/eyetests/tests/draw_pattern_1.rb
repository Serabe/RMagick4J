require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

gc = Magick::Draw.new
gc.pattern('green', 0, 0, 16, 16) do
  gc.stroke('none')
  gc.fill('DarkBlue')
  gc.rectangle(0,0, 16,16)
  gc.fill('yellow')
  gc.stroke('red')
  gc.polygon(0,0, 8,16, 16,0, 0,0)
end

gc.pattern('blue', 5, 5, 16, 16) do
  gc.stroke('none')
  gc.fill('yellow')
  gc.rectangle(0, 0, 16, 16)
  gc.fill('red')
  gc.stroke('DarkBlue')
  gc.polygon(0,16, 8,0, 16,16, 0,16)
end

gc.stroke('green')
gc.stroke_width(16)
gc.fill('blue')
gc.ellipse(150, 75, 130, 60, 0, 360)

img = Magick::Image.new(300, 150, Magick::HatchFill.new('white','LightCyan2',8))
gc.draw(img)

#img.border!(1,1, "LightCyan2")

img.write('draw_pattern_1.jpg')
exit

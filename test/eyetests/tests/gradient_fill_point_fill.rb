require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

fill = GradientFill.new(100, 100, 100, 100,'#000000','#ff8844')

image = Image.new(300,300,fill)

image.write('gradient_fill_point_fill.jpg')
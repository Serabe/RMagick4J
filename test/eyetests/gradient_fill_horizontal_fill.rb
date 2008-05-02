require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

fill = GradientFill.new(0,200,300,200,'#000000','#ff8844')

image = Image.new(300,300,fill)

image.write('gradient_fill_horizontal_fill.jpg')
require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

a = Image.new 200, 200, HatchFill.new('transparent', 'orange')
b = Image.new 100, 100, HatchFill.new('transparent', 'green', 7)
c = Image.new 300, 300, GradientFill.new(150, 150, 150, 150, 'orange', 'black')

list = ImageList.new

list << c
list << b
list << a

list.flatten_images.write('image_list_flatten_images.jpg')
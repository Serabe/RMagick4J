require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

hatch = HatchFill.new('black', 'white', 20)

img = Image.new(300, 300, hatch)

img.write('hatch_fill.jpg')
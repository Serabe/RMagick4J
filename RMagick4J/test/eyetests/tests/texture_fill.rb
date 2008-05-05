require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

texture_image = Image.load('texture.jpg').first

texture = TextureFill.new(texture_image)

image = Image.new 300, 300, texture

image.write('texture_fill.jpg')

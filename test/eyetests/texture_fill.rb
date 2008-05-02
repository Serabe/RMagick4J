require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

texture_image = ImageList.new 'textura2.jpg'

texture = TextureFill.new(texture_image)

image = Image.new 300, 300, texture

image.write('texture_fill.jpg')

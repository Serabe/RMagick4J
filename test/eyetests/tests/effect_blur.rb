require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

clown = Image.load("clown.jpg").first

clown = clown.blur_image 4.75, 2.0

clown.write "effect_blur.jpg"

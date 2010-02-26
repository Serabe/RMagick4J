require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

clown = Image.load("clown.jpg").first

clown = clown.shade(true, 50, 50)

clown.write "effect_shade.jpg"

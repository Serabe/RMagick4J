require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

clown = Image.load("clown.jpg").first

clown = clown.negate true

clown.write "effect_negate_true.jpg"

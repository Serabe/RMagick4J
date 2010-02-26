require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

clown = Image.load("clown.jpg").first

clown = clown.charcoal 5

clown.write "effect_charcoal.jpg"

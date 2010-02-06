require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

clown = Image.load("clown.jpg").first

clown = clown.edge 0.75

clown.write "effect_charcoal.jpg"

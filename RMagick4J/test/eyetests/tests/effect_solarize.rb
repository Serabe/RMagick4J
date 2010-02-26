require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

clown = Image.load("clown.jpg").first

# The parameter is quite important, 'cause in RMagick4J QuantumRange is 256 and in ImageMagick it can change.
clown = clown.solarize(27.5.to_threshold)

clown.write "effect_solarize.jpg"

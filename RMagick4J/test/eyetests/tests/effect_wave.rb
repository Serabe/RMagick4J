require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

clown = Image.load("clown.jpg").first

clown = clown.wave

clown.write "effect_wave.jpg"
